package com.mot.service.verificationToken;

import com.mot.model.AppUser;
import org.springframework.http.ResponseEntity;

public interface VerificationTokenService {
    ResponseEntity<String> confirmToken(String token);
    String generateVerificationToken(AppUser newUser);
}
