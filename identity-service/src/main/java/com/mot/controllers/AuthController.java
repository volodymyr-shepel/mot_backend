package com.mot.controllers;

import com.mot.confirmationToken.ConfirmationTokenService;
import com.mot.dtos.AppUserDTO;
import com.mot.dtos.AuthenticationRequest;
import com.mot.dtos.AuthenticationResponse;
import com.mot.dtos.ResendVerificationEmailRequest;
import com.mot.refreshToken.RefreshTokenService;
import com.mot.services.authentication.AuthenticationService;
//import com.mot.registration.services.RegistrationServiceImpl;
import com.mot.services.registration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {
    private final AuthenticationService authenticationServiceImpl;
    private final ConfirmationTokenService confirmationTokenServiceImpl;

    private final RegistrationService registrationServiceImpl;

    private final RefreshTokenService refreshTokenServiceImpl;


    public AuthController(AuthenticationService authenticationServiceImpl,
                          ConfirmationTokenService confirmationTokenServiceImpl,
                          RegistrationService registrationServiceImpl,
                          RefreshTokenService refreshTokenServiceImpl) {

        this.authenticationServiceImpl = authenticationServiceImpl;
        this.confirmationTokenServiceImpl = confirmationTokenServiceImpl;
        this.registrationServiceImpl = registrationServiceImpl;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
    }

    // used to register a user
    @PostMapping(path = "/register")
    public ResponseEntity<Integer> register(@RequestBody AppUserDTO appUserDTO){
        return registrationServiceImpl.register(appUserDTO);
    }

    @PostMapping(path = "/signIn")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        return authenticationServiceImpl.authenticate(authenticationRequest);
    }

    // used to confirm an email
    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        return confirmationTokenServiceImpl.confirmToken(token);
    }
    @PostMapping(path = "/refreshToken")
    public AuthenticationResponse refreshToken(@RequestParam UUID token){
        return refreshTokenServiceImpl.refreshToken(token);
    }
    @PostMapping(path = "/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody ResendVerificationEmailRequest resendVerificationEmailRequest){
        return authenticationServiceImpl.resendVerificationEmail(resendVerificationEmailRequest);
    }

}
