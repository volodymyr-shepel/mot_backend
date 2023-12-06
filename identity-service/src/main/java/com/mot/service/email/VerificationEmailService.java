package com.mot.service.email;

import com.mot.model.AppUser;
import com.mot.repository.ConfirmationTokenRepository;
import com.mot.service.confirmationToken.ConfirmationTokenService;
import com.mot.dtos.NotificationDTO;
import com.mot.enums.NotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class VerificationEmailService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.config.verification-email-subject}")
    private String verificationEmailSubject;

    private final ConfirmationTokenService confirmationTokenServiceImpl;

    private final ConfirmationTokenRepository confirmationTokenRepository;


    public VerificationEmailService(ConfirmationTokenService confirmationTokenServiceImpl, ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenServiceImpl = confirmationTokenServiceImpl;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public NotificationDTO buildNotificationDTO(AppUser appUser){ // build notification dto for verification email
        return new NotificationDTO(
                appUser.getUsername(),
                verificationEmailSubject,
                NotificationType.EMAIL_VERIFICATION,
                generateVerificationEmailFields(appUser)
        );

    }
    public void verifyEmailForResend(AppUser appUser){
        checkIfUserIsEnabled(appUser);
        checkIfVerificationEmailIsActive(appUser);
    }


    private HashMap<String,String> generateVerificationEmailFields(AppUser appUser){
        String token = confirmationTokenServiceImpl.generateConfirmationToken(appUser);
        String confirmationLink = generateConfirmationLink(token);

        return new HashMap<>() {{
            // there is a placeholder in html template which represents the confirmation link
            put("link", confirmationLink);
            // there is a placeholder in html template which represents the name of the user
            put("name", appUser.getFirstName());
        }};
    }

    private String generateConfirmationLink(String token) {
        return  baseUrl + "/api/auth/email/confirm?token=" + token;
    }

    private void checkIfUserIsEnabled(AppUser appUser) {
        if (appUser.isEnabled()) {
            throw new IllegalStateException("Provided account is already activated");
        }
    }

    private void checkIfVerificationEmailIsActive(AppUser appUser) {
        if (confirmationTokenRepository.existsByAppUserEmailAndExpiresAtAfter(appUser.getUsername(), LocalDateTime.now())) {
            throw new IllegalStateException("There is an active verification email associated with the provided account." +
                    " Verify it or try again in 15 minutes");
        }
    }

}
