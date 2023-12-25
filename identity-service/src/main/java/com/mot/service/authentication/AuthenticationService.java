package com.mot.service.authentication;

import com.mot.dtos.CredentialsDTO;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AuthenticationService {
    ResponseEntity<UserAuthenticationResponse> authenticate(UserAuthenticationRequest userAuthenticationRequest);

    UserAuthenticationResponse buildAuthenticationResponse(String accessToken, UUID refreshToken);

    ResponseEntity<String> validateCredentials(CredentialsDTO credentialsDTO);
}
