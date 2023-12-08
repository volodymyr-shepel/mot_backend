package com.mot.service.account;

import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.UpdatePasswordDTO;
import com.mot.exception.InvalidTokenException;
import com.mot.exception.UserNotFoundException;
import com.mot.model.AppUser;
import com.mot.model.VerificationToken;
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
        AppUser user = getAppUserByEmail(email);
        verificationTokenServiceImpl.generateVerificationToken(user);
        emailServiceImpl.sendForgetPasswordEmail(user);
        return ResponseEntity.ok("Forget Password Email was sent successfully");

    }

    @Transactional
    public ResponseEntity<String> updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        String password = updatePasswordDTO.password();
        String verificationToken = updatePasswordDTO.verificationToken();;

        VerificationToken token = getConfirmationToken(verificationToken);
        checkIfTokenAlreadySubmitted(token);
        checkIfTokenExpired(token);

        AppUser appUser = getAppUser(token);
        // validate if the new password satisfy all security requirements
        passwordValidatorImpl.validatePassword(password);

        // update password
        appUser.setPassword(encodePassword(password));

        updateVerificationToken(token);

        return ResponseEntity.ok("Password was updated successfully");
    }


    private AppUser getAppUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("There is no such user"));
    }

    private VerificationToken getConfirmationToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Confirmation token not found"));
    }

    private void checkIfTokenAlreadySubmitted(VerificationToken verificationToken) {
        if (verificationToken.getConfirmedAt() != null) {
            throw new InvalidTokenException("Confirmation token was already submitted");
        }
    }

    private void checkIfTokenExpired(VerificationToken verificationToken) {
        if (LocalDateTime.now().isAfter(verificationToken.getExpiresAt())) {
            throw new InvalidTokenException("Confirmation token has expired");
        }
    }

    private AppUser getAppUser(VerificationToken verificationToken) {
        return appUserRepository.findByEmail(verificationToken.getAppUser().getUsername())
                .orElseThrow(() -> new InvalidTokenException("There is no user associated with this confirmation token"));
    }

    private void updateVerificationToken(VerificationToken verificationToken) {
        verificationToken.setConfirmedAt(LocalDateTime.now());
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }



}
