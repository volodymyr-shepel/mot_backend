package com.mot.service.notification;

import com.mot.amqp.RabbitMQMessageProducer;
import com.mot.dtos.NotificationDTO;
import com.mot.enums.NotificationType;
import com.mot.model.AppUser;
import com.mot.repository.VerificationTokenRepository;
import com.mot.service.verificationToken.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class VerificationNotificationService extends NotificationService{

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.config.verification-email-subject}")
    private String emailSubject;

    private final NotificationType notificationType = NotificationType.EMAIL_VERIFICATION;


    private final VerificationTokenService verificationTokenServiceImpl;


    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationNotificationService(
            RabbitMQMessageProducer rabbitMQMessageProducer,
            VerificationTokenService verificationTokenServiceImpl,
            VerificationTokenRepository verificationTokenRepository) {
        super(rabbitMQMessageProducer);
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public void sendVerificationNotification(AppUser appUser){
        NotificationDTO notificationDTO = buildNotificationDTO(appUser);
        publishNotification(notificationDTO);
    }

    public NotificationDTO buildNotificationDTO(AppUser appUser){
        return NotificationDTO.builder()
                .recipient(appUser.getUsername())
                .subject(emailSubject)
                .notificationType(notificationType)
                .notificationFields(generateVerificationEmailFields(appUser))
                .build();
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
        return  baseUrl + "/api/auth/account/v1/activate?token=" + token;
    }

    public void verifyEmailForResend(AppUser appUser){
        checkIfUserIsEnabled(appUser);
        checkIfVerificationEmailIsActive(appUser);
    }


    private void checkIfUserIsEnabled(AppUser appUser) {
        if (appUser.isEnabled()) {
            throw new IllegalStateException("Provided account is already activated");
        }
    }

    private void checkIfVerificationEmailIsActive(AppUser appUser) {
        if (verificationTokenRepository.existsByAppUserEmailAndExpiresAtAfterAndConfirmedAtIsNull(appUser.getUsername(), LocalDateTime.now())) {
            throw new IllegalStateException("There is an active verification email associated with the provided account." +
                    " Verify it or try again in 15 minutes");
        }
    }


}