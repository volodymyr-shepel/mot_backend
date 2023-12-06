package com.mot.service.confirmationToken;

import com.mot.model.AppUser;
import org.springframework.http.ResponseEntity;

public interface ConfirmationTokenService {
    ResponseEntity<String> confirmToken(String token);
    String generateConfirmationToken(AppUser newUser);
}
