package com.mot.confirmationToken;


import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.exceptions.InvalidConfirmationTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository, AppUserRepository appUserRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.appUserRepository = appUserRepository;
    }

    public String generateConfirmationToken(AppUser newUser) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), // confirmation token will be valid for 15 minutes
                newUser
        );
        confirmationTokenRepository.save(confirmationToken);
        return token;
    }


    @Transactional
    // used to verify if the confirmation token is valid, and if it is - enable user account
    public ResponseEntity<String> confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new InvalidConfirmationTokenException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new InvalidConfirmationTokenException("token was already submitted");
        }

        if (LocalDateTime.now().isAfter(confirmationToken.getExpiresAt())) {
            throw new InvalidConfirmationTokenException("Token has expired");
        }


        AppUser appUser = appUserRepository.findByEmail(
                confirmationToken.getAppUser().getUsername()).orElseThrow(() ->
                new InvalidConfirmationTokenException("There is no user associated with this confirmation token"));

        confirmationToken.setConfirmedAt(LocalDateTime.now());

        appUser.setIsEnabled(true);


        return ResponseEntity.ok("Email has been successfully confirmed");
    }
}