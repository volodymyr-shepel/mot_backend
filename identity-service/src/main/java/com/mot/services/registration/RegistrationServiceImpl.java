package com.mot.services.registration;

import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.dtos.AppUserDTO;
import com.mot.services.email.EmailService;
import com.mot.services.email.VerificationEmailService;
import com.mot.util.PasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService{
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public RegistrationServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public ResponseEntity<Integer> register(AppUserDTO appUserDTO) {

        // used to validate the password
        PasswordValidator.validate(appUserDTO.password());

        // Creating AppUser instance from the DTO object
        AppUser createdUser = createAppUserFromAppUserDTO(appUserDTO);

        // Saves newly created user and used to extract user id which will be returned in ResponseEntity
        Integer userId = appUserRepository.saveAndFlush(createdUser).getId();

        // used to send verification email
        emailService.sendVerificationEmail(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userId);
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private AppUser createAppUserFromAppUserDTO(AppUserDTO appUserDTO) {
        String encodedPassword = encodePassword(appUserDTO.password());
        return new AppUser(
                appUserDTO.email(),
                appUserDTO.firstName(),
                appUserDTO.lastName(),
                encodedPassword,
                appUserDTO.userRole()
        );
    }





}
