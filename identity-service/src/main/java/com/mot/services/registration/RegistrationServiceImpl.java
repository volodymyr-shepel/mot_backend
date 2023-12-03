package com.mot.services.registration;

import com.mot.amqp.RabbitMQMessageProducer;
import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.confirmationToken.ConfirmationTokenServiceImpl;
import com.mot.dtos.AppUserDTO;
import com.mot.dtos.NotificationDTO;
import com.mot.util.NotificationType;
import com.mot.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;


@Service
public class RegistrationServiceImpl implements RegistrationService{
    private final PasswordValidator passwordValidator;
    private final AppUserRepository appUserRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenServiceImpl confirmationTokenServiceImpl;


    @Value("${app.rabbitmq.internal-exchange}")
    private String internalExchange;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.config.verification-email-subject}")
    private String verificationEmailSubject;

    @Autowired
    public RegistrationServiceImpl(PasswordValidator passwordValidator, AppUserRepository appUserRepository, RabbitMQMessageProducer rabbitMQMessageProducer, PasswordEncoder passwordEncoder, ConfirmationTokenServiceImpl confirmationTokenServiceImpl) {
        this.passwordValidator = passwordValidator;
        this.appUserRepository = appUserRepository;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenServiceImpl = confirmationTokenServiceImpl;
    }


    @Transactional
    public ResponseEntity<Integer> register(AppUserDTO appUserDTO) {

        // used to validate the password
        passwordValidator.validate(appUserDTO.password());

        // Creating AppUser instance from the DTO object
        AppUser createdUser = createUserFromDTO(appUserDTO);

        // Saves newly created user and used to extract user id which will be returned in ResponseEntity
        Integer userId = appUserRepository.saveAndFlush(createdUser).getId();

        //  NotificationDTO which will be sent to email service
        // last hash map is map of fields in thymeleaf template, so the name
        NotificationDTO notification = new NotificationDTO(
                createdUser.getUsername(),
                verificationEmailSubject,
                NotificationType.EMAIL_VERIFICATION,
                generateVerificationEmailFields(createdUser)
        );


        rabbitMQMessageProducer.publish(
                notification,
                internalExchange,
                emailRoutingKey
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userId);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }


    public AppUser createUserFromDTO(AppUserDTO appUserDTO) {
        String encodedPassword = encodePassword(appUserDTO.password());
        return new AppUser(
                appUserDTO.email(),
                appUserDTO.firstName(),
                appUserDTO.lastName(),
                encodedPassword,
                appUserDTO.userRole()
        );
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
        return  baseUrl + "/api/auth/confirm?token=" + token;
    }


}
