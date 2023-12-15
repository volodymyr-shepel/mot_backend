package com.mot.controller;

import com.mot.dtos.AppUserDTO;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
import com.mot.service.authentication.AuthenticationService;
import com.mot.service.registration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/auth/user")
public class AuthController {
    private final RegistrationService registrationServiceImpl;
    private final AuthenticationService authenticationServiceImpl;

    public AuthController(RegistrationService registrationServiceImpl,
                          AuthenticationService authenticationServiceImpl) {
        this.registrationServiceImpl = registrationServiceImpl;
        this.authenticationServiceImpl = authenticationServiceImpl;
    }
    @PostMapping(path = "/signUp")
    public ResponseEntity<UUID> signUp(@RequestBody AppUserDTO appUserDTO){
        return registrationServiceImpl.signUp(appUserDTO);
    }


    @PostMapping(path = "/signIn")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@RequestBody UserAuthenticationRequest userAuthenticationRequest){
        return authenticationServiceImpl.authenticate(userAuthenticationRequest);
    }





}
