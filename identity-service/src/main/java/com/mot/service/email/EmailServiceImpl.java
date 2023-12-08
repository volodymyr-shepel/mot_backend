package com.mot.service.email;

import com.mot.amqp.RabbitMQMessageProducer;
import com.mot.model.AppUser;
import com.mot.repository.AppUserRepository;
import com.mot.service.verificationToken.VerificationTokenService;
import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.NotificationDTO;
import com.mot.exception.UserNotFoundException;
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
    private final VerificationTokenService verificationTokenServiceImpl;
    private final AppUserRepository appUserRepository;

    private final VerificationEmailService verificationEmailService;
    private final ResetPasswordEmailService resetPasswordEmailService;

    public EmailServiceImpl(RabbitMQMessageProducer rabbitMQMessageProducer, VerificationTokenService verificationTokenServiceImpl, AppUserRepository appUserRepository, VerificationEmailService verificationEmailService, ResetPasswordEmailService resetPasswordEmailService) {
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;
        this.appUserRepository = appUserRepository;
        this.verificationEmailService = verificationEmailService;
        this.resetPasswordEmailService = resetPasswordEmailService;
    }

    @Override
    public ResponseEntity<String> confirmEmail(String token) {
        return verificationTokenServiceImpl.confirmToken(token);
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


    public void sendForgetPasswordEmail(AppUser appUser){
        NotificationDTO resetPasswordEmailNotification = resetPasswordEmailService.buildNotificationDTO(appUser);
        sendEmail(resetPasswordEmailNotification);
    }




    private AppUser getAppUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("There is no such user"));
    }





}
