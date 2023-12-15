package com.mot.service.notification;

import com.mot.amqp.RabbitMQMessageProducer;
import com.mot.dtos.NotificationDTO;
import com.mot.enums.NotificationType;
import com.mot.model.AppUser;
import com.mot.service.verificationToken.VerificationTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ResetPasswordNotificationService extends NotificationService{

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.config.forget-password-email-subject}")
    private String emailSubject;

    private final VerificationTokenService verificationTokenServiceImpl;

    public ResetPasswordNotificationService(
            RabbitMQMessageProducer rabbitMQMessageProducer,
            VerificationTokenService verificationTokenServiceImpl) {
        super(rabbitMQMessageProducer);
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;

    }
    public void sendResetPasswordNotification(AppUser appUser){
        NotificationDTO notificationDTO = buildNotificationDTO(appUser);
        publishNotification(notificationDTO);
    }


    public NotificationDTO buildNotificationDTO(AppUser appUser){
        return NotificationDTO.builder()
                .recipient(appUser.getUsername())
                .subject(emailSubject)
                .notificationType(NotificationType.RESET_PASSWORD)
                .notificationFields(generateNotificationFields(appUser))
                .build();
    }
    private HashMap<String,String> generateNotificationFields(AppUser appUser){
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
        return  baseUrl + "/api/auth/account/v1/reset-password?token=" + token;
    }
}
