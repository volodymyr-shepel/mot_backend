package com.mot.confirmationToken;

import com.mot.appUser.AppUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ConfirmationTokenService {
    ResponseEntity<String> confirmToken(String token);
    String generateConfirmationToken(AppUser newUser);
}
