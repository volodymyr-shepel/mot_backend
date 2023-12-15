package com.mot.controller;

import com.mot.dtos.UserAuthenticationResponse;
import com.mot.service.refreshToken.RefreshTokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/auth/token")
public class TokenController {
    private final RefreshTokenService refreshTokenServiceImpl;

    public TokenController(RefreshTokenService refreshTokenServiceImpl) {
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
    }

    @PostMapping(path = "/refresh")
    public UserAuthenticationResponse refreshToken(@RequestParam UUID token){
        return refreshTokenServiceImpl.refreshToken(token);
    }

}
