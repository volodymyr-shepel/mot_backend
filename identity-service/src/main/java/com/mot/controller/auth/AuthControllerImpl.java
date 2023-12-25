package com.mot.controller.auth;

import com.mot.controller.auth.AuthController;
import com.mot.dtos.AppUserDTO;
import com.mot.dtos.CredentialsDTO;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
import com.mot.service.authentication.AuthenticationService;
import com.mot.service.registration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/auth/user/v1")
public class AuthControllerImpl implements AuthController {
    private final RegistrationService registrationServiceImpl;
    private final AuthenticationService authenticationServiceImpl;

    public AuthControllerImpl(RegistrationService registrationServiceImpl,
                              AuthenticationService authenticationServiceImpl) {
        this.registrationServiceImpl = registrationServiceImpl;
        this.authenticationServiceImpl = authenticationServiceImpl;
    }
    @Override
    public ResponseEntity<UUID> signUp(@RequestBody AppUserDTO appUserDTO){
        return registrationServiceImpl.signUp(appUserDTO);
    }

    @Override
    public ResponseEntity<UserAuthenticationResponse> authenticate(@RequestBody UserAuthenticationRequest userAuthenticationRequest){
        return authenticationServiceImpl.authenticate(userAuthenticationRequest);
    }

    @Override
    public ResponseEntity<String> validateCredentials(@RequestBody CredentialsDTO credentialsDTO) {
        return authenticationServiceImpl.validateCredentials(credentialsDTO);
    }


}
