package com.mot.service.verificationToken;


import com.mot.model.AppUser;
import com.mot.repository.AppUserRepository;
import com.mot.model.VerificationToken;
import com.mot.repository.VerificationTokenRepository;
import com.mot.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final AppUserRepository appUserRepository;

    @Value("${app.security.confirmation-token.expires-in}")
    private long confirmationTokenExpiresIn; // the value is provided in seconds



    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository, AppUserRepository appUserRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.appUserRepository = appUserRepository;
    }




    @Transactional
    // used to verify if the confirmation token is valid, and if it is - enable user account
    public ResponseEntity<String> confirmToken(String token) {
        VerificationToken verificationToken = getConfirmationToken(token);
        checkIfTokenAlreadySubmitted(verificationToken);
        checkIfTokenExpired(verificationToken);

        AppUser appUser = getAppUser(verificationToken);
        updateConfirmationToken(verificationToken);
        enableUserAccount(appUser);

        return ResponseEntity.ok("Email has been successfully confirmed");
    }

    public String generateVerificationToken(AppUser newUser) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(confirmationTokenExpiresIn), // confirmation token will be valid for 15 minutes
                newUser
        );


        verificationTokenRepository.save(verificationToken);
        return token;
    }


    private VerificationToken getConfirmationToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Confirmation token not found"));
    }

    private void checkIfTokenAlreadySubmitted(VerificationToken verificationToken) {
        if (verificationToken.getConfirmedAt() != null) {
            throw new InvalidTokenException("Confirmation token was already submitted");
        }
    }

    private void checkIfTokenExpired(VerificationToken verificationToken) {
        if (LocalDateTime.now().isAfter(verificationToken.getExpiresAt())) {
            throw new InvalidTokenException("Confirmation token has expired");
        }
    }

    private AppUser getAppUser(VerificationToken verificationToken) {
        return appUserRepository.findByEmail(verificationToken.getAppUser().getUsername())
                .orElseThrow(() -> new InvalidTokenException("There is no user associated with this confirmation token"));
    }

    private void updateConfirmationToken(VerificationToken verificationToken) {
        verificationToken.setConfirmedAt(LocalDateTime.now());
    }

    private void enableUserAccount(AppUser appUser) {
        appUser.setEnabled(true);
    }



}