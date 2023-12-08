package com.mot.service.email;

import com.mot.dtos.NotificationDTO;
import com.mot.enums.NotificationType;
import com.mot.model.AppUser;
import com.mot.service.verificationToken.VerificationTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ResetPasswordEmailService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.config.forget-password-email-subject}")
    private String resetPasswordEmailSubject;

    private final VerificationTokenService verificationTokenServiceImpl;

    public ResetPasswordEmailService(VerificationTokenService verificationTokenServiceImpl) {
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;
    }

    public NotificationDTO buildNotificationDTO(AppUser appUser){ // build notification dto for verification email
        return NotificationDTO.builder()
                .recipient(appUser.getUsername())
                .subject(resetPasswordEmailSubject)
                .notificationType(NotificationType.RESET_PASSWORD)
                .notificationFields(generateResetPasswordEmailFields(appUser))
                .build();
    }
    private HashMap<String,String> generateResetPasswordEmailFields(AppUser appUser){
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
        return  baseUrl + "/api/auth/account/reset-password?token=" + token;
    }
}
