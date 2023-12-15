package com.mot.service.refreshToken;

import com.mot.model.AppUser;
import com.mot.repository.AppUserRepository;
import com.mot.dtos.UserAuthenticationResponse;
import com.mot.model.token.RefreshToken;
import com.mot.repository.RefreshTokenRepository;
import com.mot.service.authentication.AuthenticationService;
import com.mot.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AppUserRepository appUserRepository;

    private final AuthenticationService authenticationServiceImpl;

    @Autowired
    public RefreshTokenServiceImpl(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, AppUserRepository appUserRepository, AuthenticationService authenticationServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.appUserRepository = appUserRepository;
        this.authenticationServiceImpl = authenticationServiceImpl;
    }


    public UserAuthenticationResponse refreshToken(UUID token) {
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByIdOrThrowAnException(token);
        String extractEmailFromToken = refreshToken.extractEmailFromToken();
        AppUser appUser = appUserRepository.getAppUserByEmailOrThrowUserNotFoundException(extractEmailFromToken);
        refreshToken.validateTokenExpiration();
        String accessToken = jwtUtil.generateAccessToken(appUser);
        return authenticationServiceImpl.buildAuthenticationResponse(accessToken,token);
    }


}
