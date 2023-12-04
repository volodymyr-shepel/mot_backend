package com.mot.services.email;

import com.mot.amqp.RabbitMQMessageProducer;
import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.confirmationToken.ConfirmationTokenService;
import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.NotificationDTO;
import com.mot.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("motEmailService")
public class EmailServiceImpl implements EmailService{
    @Value("${app.rabbitmq.internal-exchange}")
    private String internalExchange;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final ConfirmationTokenService confirmationTokenServiceImpl;
    private final AppUserRepository appUserRepository;

    private final VerificationEmailService verificationEmailService;

    public EmailServiceImpl(RabbitMQMessageProducer rabbitMQMessageProducer, ConfirmationTokenService confirmationTokenServiceImpl, AppUserRepository appUserRepository, VerificationEmailService verificationEmailService) {
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
        this.confirmationTokenServiceImpl = confirmationTokenServiceImpl;
        this.appUserRepository = appUserRepository;
        this.verificationEmailService = verificationEmailService;
    }

    @Override
    public ResponseEntity<String> confirmEmail(String token) {
        return confirmationTokenServiceImpl.confirmToken(token);
    }
    @Override
    public ResponseEntity<String> resendVerificationEmail(EmailAddressDTO emailAddressDTO) {
        String email = emailAddressDTO.email();
        AppUser appUser = getAppUserByEmail(email);

        verificationEmailService.verifyEmailForResend(appUser);

        sendVerificationEmail(appUser);

        return ResponseEntity.ok("Email was sent successfully");

    }

    @Override
    public void sendEmail(NotificationDTO notification){
        rabbitMQMessageProducer.publish(
                notification,
                internalExchange,
                emailRoutingKey
        );
    }

    @Override
    public void sendVerificationEmail(AppUser appUser) {
        NotificationDTO verificationEmailNotification = verificationEmailService.buildNotificationDTO(appUser);
        sendEmail(verificationEmailNotification);
    }




    private AppUser getAppUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("There is no such user"));
    }





}
