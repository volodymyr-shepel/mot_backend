package com.mot.controller.auth;



import com.mot.dtos.AppUserDTO;
import com.mot.dtos.CredentialsDTO;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

public interface AuthController {

    @PostMapping(path = "/signUp")
    ResponseEntity<UUID> signUp(AppUserDTO appUserDTO);

    @PostMapping(path = "/signIn")
    ResponseEntity<UserAuthenticationResponse> authenticate(UserAuthenticationRequest userAuthenticationRequest);


    @PostMapping(path = "/validateCredentials")
    ResponseEntity<String> validateCredentials(CredentialsDTO credentialsDTO);
}

