package com.mot.refreshToken;

import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.dtos.UserAuthenticationResponse;
import com.mot.exceptions.InvalidTokenException;
import com.mot.services.authentication.AuthenticationService;
import com.mot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        RefreshToken refreshToken = getRefreshToken(token);
        AppUser appUser = getAppUser(refreshToken);
        validateTokenExpiration(refreshToken);

        String accessToken = jwtUtil.generateAccessToken(appUser);

        return authenticationServiceImpl.buildAuthenticationResponse(accessToken,token);
    }

    private RefreshToken getRefreshToken(UUID token) {
        return refreshTokenRepository.findById(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
    }

    private AppUser getAppUser(RefreshToken refreshToken) {
        return appUserRepository.findByEmail(
                        refreshToken.getAppUser().getUsername())
                .orElseThrow(() -> new InvalidTokenException("There is no user associated with this refresh token"));
    }

    private void validateTokenExpiration(RefreshToken refreshToken) {
        if (LocalDateTime.now().isAfter(refreshToken.getExpiresAt())) {
            throw new InvalidTokenException("Token has expired");
        }
    }
}
