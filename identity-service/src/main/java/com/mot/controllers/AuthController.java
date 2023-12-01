package com.mot.controllers;

import com.mot.confirmationToken.ConfirmationTokenService;
import com.mot.dtos.AppUserDTO;
import com.mot.dtos.AuthenticationRequest;
import com.mot.dtos.AuthenticationResponse;
import com.mot.dtos.RefreshTokenRequest;
import com.mot.refreshToken.RefreshTokenService;
import com.mot.services.AuthenticationService;
//import com.mot.services.RegistrationService;
import com.mot.services.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;

    private final RegistrationService registrationService;

    private final RefreshTokenService refreshTokenService;


    @Autowired
    public AuthController(AuthenticationService authenticationService, ConfirmationTokenService confirmationTokenService, RegistrationService registrationService, RefreshTokenService refreshTokenService) {
        this.authenticationService = authenticationService;
        this.confirmationTokenService = confirmationTokenService;
        this.registrationService = registrationService;
        this.refreshTokenService = refreshTokenService;
    }

    // used to register a user
    @PostMapping(path = "/register")
    public ResponseEntity<Integer> register(@RequestBody AppUserDTO appUserDTO){
        return registrationService.register(appUserDTO);
    }

    @PostMapping(path = "/signIn")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        return authenticationService.authenticate(authenticationRequest);
    }

    // used to confirm an email
    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }
    @PostMapping(path = "/refreshToken")
    public AuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return refreshTokenService.refreshToken(refreshTokenRequest);
    }

}
