package com.mot.util.passwordValidator;

import com.mot.exception.PasswordValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidatorImpl implements PasswordValidator{
    public void validatePassword(String password) throws PasswordValidationException {
        // Minimum 12 characters
        if (password.length() < 12) {
            throw new PasswordValidationException("Password must be at least 12 characters long.");
        }

        // A combination of uppercase, lowercase, letters, and at least one special character
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{12,}$");
        if (!pattern.matcher(password).matches()) {
            throw new PasswordValidationException("Password must include uppercase and lowercase letters, a special character, and be at least 12 characters long.");
        }
    }
}