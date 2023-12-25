package com.mot.service.verificationToken;


import com.mot.model.AppUser;
import com.mot.repository.AppUserRepository;
import com.mot.model.token.VerificationToken;
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
    public ResponseEntity<String> confirmToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.getVerificationTokenOrThrowAnException(token);
        validateToken(verificationToken);
        String extractedEmail = extractEmailFromToken(verificationToken);
        AppUser appUser = appUserRepository.getAppUserByEmailOrThrowUserNotFoundException(extractedEmail);
        verificationToken.setConfirmedAt(LocalDateTime.now());
        appUser.setEnabled(true);
        return ResponseEntity.ok("Email has been successfully confirmed");
    }

    private void validateToken(VerificationToken verificationToken) {
        checkIfTokenAlreadySubmitted(verificationToken);
        checkIfTokenExpired(verificationToken);
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
    private String extractEmailFromToken(VerificationToken verificationToken) {
        return verificationToken.getAppUser().getUsername();
    }





}