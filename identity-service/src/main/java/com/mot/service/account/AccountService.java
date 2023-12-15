package com.mot.service.account;

import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.UpdatePasswordDTO;
import com.mot.exception.InvalidTokenException;
import com.mot.model.AppUser;
import com.mot.model.token.VerificationToken;
import com.mot.repository.AppUserRepository;
import com.mot.repository.VerificationTokenRepository;
import com.mot.service.email.EmailService;
import com.mot.service.verificationToken.VerificationTokenService;
import com.mot.util.passwordValidator.PasswordValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AccountService {

    private final AppUserRepository appUserRepository;

    private final VerificationTokenService verificationTokenServiceImpl;

    private final EmailService emailServiceImpl;

    private final PasswordValidator passwordValidatorImpl;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenRepository verificationTokenRepository;

    public AccountService(AppUserRepository appUserRepository, VerificationTokenService verificationTokenServiceImpl, EmailService emailServiceImpl, PasswordValidator passwordValidatorImpl, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository) {
        this.appUserRepository = appUserRepository;
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;
        this.emailServiceImpl = emailServiceImpl;
        this.passwordValidatorImpl = passwordValidatorImpl;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public ResponseEntity<String> forgetPassword(EmailAddressDTO emailAddressDTO) {
        String email = emailAddressDTO.email();
        AppUser user = appUserRepository.getAppUserByEmailOrThrowUserNotFoundException(email);
        verificationTokenServiceImpl.generateVerificationToken(user);
        emailServiceImpl.sendForgetPasswordEmail(user);
        return ResponseEntity.ok("Forget Password Email was sent successfully");

    }

    @Transactional
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

    public ResponseEntity<String> activateAccount(String token) {
        return verificationTokenServiceImpl.confirmToken(token);
    }



    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }



}
