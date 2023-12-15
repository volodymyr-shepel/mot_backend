package com.mot.service.registration;

import com.mot.model.AppUser;
import com.mot.repository.AppUserRepository;
import com.mot.dtos.AppUserDTO;
import com.mot.service.notification.VerificationNotificationService;
import com.mot.util.passwordValidator.PasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService{
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final VerificationNotificationService verificationNotificationService;

    public RegistrationServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, VerificationNotificationService verificationNotificationService, PasswordValidator passwordValidatorImpl) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationNotificationService = verificationNotificationService;
        this.passwordValidatorImpl = passwordValidatorImpl;
    }

    private final PasswordValidator passwordValidatorImpl;




    @Transactional
    public ResponseEntity<UUID> signUp(AppUserDTO appUserDTO) {

        // used to validatePassword the password
        passwordValidatorImpl.validatePassword(appUserDTO.password());

        // Creating AppUser instance from the DTO object
        AppUser createdUser = createAppUserFromAppUserDTO(appUserDTO);

        // Saves newly created user and used to extract user id which will be returned in ResponseEntity
        UUID userId = appUserRepository.saveAndFlush(createdUser).getId();


        verificationNotificationService.sendVerificationNotification(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
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
    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }





}
