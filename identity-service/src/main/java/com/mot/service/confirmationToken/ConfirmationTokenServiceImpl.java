package com.mot.service.confirmationToken;


import com.mot.model.AppUser;
import com.mot.repository.AppUserRepository;
import com.mot.model.ConfirmationToken;
import com.mot.repository.ConfirmationTokenRepository;
import com.mot.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService{

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AppUserRepository appUserRepository;

    @Value("${app.security.confirmation-token.expires-in}")
    private long confirmationTokenExpiresIn; // the value is provided in seconds



    @Autowired
    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository, AppUserRepository appUserRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.appUserRepository = appUserRepository;
    }




    @Transactional
    // used to verify if the confirmation token is valid, and if it is - enable user account
    public ResponseEntity<String> confirmToken(String token) {
        ConfirmationToken confirmationToken = getConfirmationToken(token);
        checkIfTokenAlreadySubmitted(confirmationToken);
        checkIfTokenExpired(confirmationToken);

        AppUser appUser = getAppUser(confirmationToken);
        updateConfirmationToken(confirmationToken);
        enableUserAccount(appUser);

        return ResponseEntity.ok("Email has been successfully confirmed");
    }

    public String generateConfirmationToken(AppUser newUser) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(confirmationTokenExpiresIn), // confirmation token will be valid for 15 minutes
                newUser
        );
        confirmationTokenRepository.save(confirmationToken);
        return token;
    }


    private ConfirmationToken getConfirmationToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Confirmation token not found"));
    }

    private void checkIfTokenAlreadySubmitted(ConfirmationToken confirmationToken) {
        if (confirmationToken.getConfirmedAt() != null) {
            throw new InvalidTokenException("Confirmation token was already submitted");
        }
    }

    private void checkIfTokenExpired(ConfirmationToken confirmationToken) {
        if (LocalDateTime.now().isAfter(confirmationToken.getExpiresAt())) {
            throw new InvalidTokenException("Confirmation token has expired");
        }
    }

    private AppUser getAppUser(ConfirmationToken confirmationToken) {
        return appUserRepository.findByEmail(confirmationToken.getAppUser().getUsername())
                .orElseThrow(() -> new InvalidTokenException("There is no user associated with this confirmation token"));
    }

    private void updateConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationToken.setConfirmedAt(LocalDateTime.now());
    }

    private void enableUserAccount(AppUser appUser) {
        appUser.setIsEnabled(true);
    }



}