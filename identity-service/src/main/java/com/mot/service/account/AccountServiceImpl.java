package com.mot.service.account;

import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.UpdatePasswordDTO;
import com.mot.model.AppUser;
import com.mot.model.token.VerificationToken;
import com.mot.repository.AppUserRepository;
import com.mot.repository.VerificationTokenRepository;
import com.mot.service.notification.ResetPasswordNotificationService;
import com.mot.service.notification.VerificationNotificationService;
import com.mot.service.verificationToken.VerificationTokenService;
import com.mot.util.passwordValidator.PasswordValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService{

    private final AppUserRepository appUserRepository;

    private final VerificationTokenService verificationTokenServiceImpl;

    private final VerificationNotificationService verificationNotificationService;

    private final ResetPasswordNotificationService resetPasswordNotificationService;

    private final PasswordValidator passwordValidatorImpl;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenRepository verificationTokenRepository;

    public AccountServiceImpl(AppUserRepository appUserRepository, VerificationTokenService verificationTokenServiceImpl, VerificationNotificationService verificationNotificationService, ResetPasswordNotificationService resetPasswordNotificationService, PasswordValidator passwordValidatorImpl, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository) {
        this.appUserRepository = appUserRepository;
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;
        this.verificationNotificationService = verificationNotificationService;
        this.resetPasswordNotificationService = resetPasswordNotificationService;
        this.passwordValidatorImpl = passwordValidatorImpl;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    @Override
    public ResponseEntity<String> forgetPassword(EmailAddressDTO emailAddressDTO) {
        String email = emailAddressDTO.email();
        AppUser user = appUserRepository.getAppUserByEmailOrThrowUserNotFoundException(email);
        verificationTokenServiceImpl.generateVerificationToken(user);
        resetPasswordNotificationService.sendResetPasswordNotification(user);
        return ResponseEntity.ok("Forget Password Email was sent successfully");

    }

    @Transactional
    @Override
    public ResponseEntity<String> updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        String providedPassword = updatePasswordDTO.password();

        String providedVerificationToken = updatePasswordDTO.verificationToken();

        VerificationToken verificationToken = verificationTokenRepository.getVerificationTokenOrThrowAnException(providedVerificationToken);

        verificationToken.validateToken();

        String extractedEmail = verificationToken.extractEmailFromToken();

        AppUser appUser = appUserRepository.getAppUserByEmailOrThrowUserNotFoundException(extractedEmail);

        passwordValidatorImpl.validatePassword(providedPassword);

        appUser.setPassword(encodePassword(providedPassword));

        verificationToken.setConfirmedAt(LocalDateTime.now());

        return ResponseEntity.ok("Password was updated successfully");
    }

    @Override
    public ResponseEntity<String> activateAccount(String token) {
        return verificationTokenServiceImpl.confirmToken(token);
    }

    @Override
    public ResponseEntity<String> resendVerificationEmail(EmailAddressDTO emailAddressDTO) {
        String providedEmail = emailAddressDTO.email();

        AppUser appUser = appUserRepository.getAppUserByEmailOrThrowUserNotFoundException(providedEmail);
        verifyEmailForResend(appUser);
        verificationNotificationService.verifyEmailForResend(appUser);
        verificationNotificationService.sendVerificationNotification(appUser);
        return ResponseEntity.ok("Email was sent successfully");

    }


    public ResponseEntity<String> resetPassword(String token) {
        VerificationToken verificationToken = verificationTokenRepository.
                getVerificationTokenOrThrowAnException(token);

        verificationToken.validateToken();

        return ResponseEntity.ok("Token was successfully verified");
    }


    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void verifyEmailForResend(AppUser appUser){
        checkIfUserIsEnabled(appUser);
        checkIfVerificationEmailIsActive(appUser);
    }


    private void checkIfUserIsEnabled(AppUser appUser) {
        if (appUser.isEnabled()) {
            throw new IllegalStateException("Provided account is not activated");
        }
    }

    private void checkIfVerificationEmailIsActive(AppUser appUser) {
        if (verificationTokenRepository.existsByAppUserEmailAndExpiresAtAfterAndConfirmedAtIsNull(appUser.getUsername(), LocalDateTime.now())) {
            throw new IllegalStateException("There is an active forget password email associated with the provided account." +
                    " Use it or try again in 15 minutes");
        }
    }



}
