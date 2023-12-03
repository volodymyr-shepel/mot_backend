package com.mot.services.authentication;

import com.mot.dtos.AuthenticationRequest;
import com.mot.dtos.AuthenticationResponse;
import com.mot.dtos.ResendVerificationEmailRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest);

    ResponseEntity<String> resendVerificationEmail(ResendVerificationEmailRequest resendVerificationEmailRequest);
}
