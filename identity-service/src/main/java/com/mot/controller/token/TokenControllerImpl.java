package com.mot.controller.token;

import com.mot.dtos.UserAuthenticationResponse;
import com.mot.service.refreshToken.RefreshTokenService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/auth/token/v1")
public class TokenControllerImpl implements TokenController{
    private final RefreshTokenService refreshTokenServiceImpl;

    public TokenControllerImpl(RefreshTokenService refreshTokenServiceImpl) {
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
    }

    @Override
    public UserAuthenticationResponse refreshToken(@RequestParam UUID token){
        return refreshTokenServiceImpl.refreshToken(token);
    }

}
