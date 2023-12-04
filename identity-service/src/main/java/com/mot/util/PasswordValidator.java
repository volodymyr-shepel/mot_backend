package com.mot.util;

import com.mot.exceptions.PasswordValidationException;

import java.util.regex.Pattern;

public class PasswordValidator {
    public static void validate(String password) throws PasswordValidationException {
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