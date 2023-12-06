package com.mot.controller;

import com.mot.dtos.AppUserDTO;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
import com.mot.service.authentication.AuthenticationService;
//import com.mot.registration.services.RegistrationServiceImpl;
import com.mot.service.registration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth/user")
public class UserAuthController {
    private final RegistrationService registrationServiceImpl;
    private final AuthenticationService authenticationServiceImpl;

    public UserAuthController(RegistrationService registrationServiceImpl,
                              AuthenticationService authenticationServiceImpl) {
        this.registrationServiceImpl = registrationServiceImpl;
        this.authenticationServiceImpl = authenticationServiceImpl;
    }

    // used to register a user
    @PostMapping(path = "/register")
    public ResponseEntity<Integer> register(@RequestBody AppUserDTO appUserDTO){
        return registrationServiceImpl.register(appUserDTO);
    }

    // used to authenticate the user
    @PostMapping(path = "/signIn")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@RequestBody UserAuthenticationRequest userAuthenticationRequest){
        return authenticationServiceImpl.authenticate(userAuthenticationRequest);
    }





}
