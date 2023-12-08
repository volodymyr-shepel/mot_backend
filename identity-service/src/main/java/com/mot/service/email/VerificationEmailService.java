package com.mot.service.email;

import com.mot.model.AppUser;
import com.mot.repository.VerificationTokenRepository;
import com.mot.service.verificationToken.VerificationTokenService;
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

    private final VerificationTokenService verificationTokenServiceImpl;

    private final VerificationTokenRepository verificationTokenRepository;


    public VerificationEmailService(VerificationTokenService verificationTokenServiceImpl, VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public NotificationDTO buildNotificationDTO(AppUser appUser){ // build notification dto for verification email
        return NotificationDTO.builder()
                .recipient(appUser.getUsername())
                .subject(verificationEmailSubject)
                .notificationType(NotificationType.EMAIL_VERIFICATION)
                .notificationFields(generateVerificationEmailFields(appUser))
                .build();
    }

    public void verifyEmailForResend(AppUser appUser){
        checkIfUserIsEnabled(appUser);
        checkIfVerificationEmailIsActive(appUser);
    }


    private HashMap<String,String> generateVerificationEmailFields(AppUser appUser){
        String token = verificationTokenServiceImpl.generateVerificationToken(appUser);
        String verificationLink = generateVerificationLink(token);

        return new HashMap<>() {{
            // there is a placeholder in html template which represents the confirmation link
            put("link", verificationLink);
            // there is a placeholder in html template which represents the name of the user
            put("name", appUser.getFirstName());
        }};
    }

    private String generateVerificationLink(String token) {
        return  baseUrl + "/api/auth/email/confirm?token=" + token;
    }

    private void checkIfUserIsEnabled(AppUser appUser) {
        if (appUser.isEnabled()) {
            throw new IllegalStateException("Provided account is already activated");
        }
    }

    private void checkIfVerificationEmailIsActive(AppUser appUser) {
        if (verificationTokenRepository.existsByAppUserEmailAndExpiresAtAfter(appUser.getUsername(), LocalDateTime.now())) {
            throw new IllegalStateException("There is an active verification email associated with the provided account." +
                    " Verify it or try again in 15 minutes");
        }
    }

}
