package com.mot.controller.token;

import com.mot.dtos.UserAuthenticationResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface TokenController {

    @PostMapping(path = "/refresh")
    UserAuthenticationResponse refreshToken(@RequestParam UUID token);
}