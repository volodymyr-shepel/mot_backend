package com.mot.refreshToken;

import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.dtos.AuthenticationResponse;
import com.mot.exceptions.InvalidConfirmationTokenException;
import com.mot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AppUserRepository appUserRepository;

    @Value("${app.security.jwt.expires-in}")
    private Integer accessTokenExpiresIn;

    @Value("${app.security.jwt.refresh-token.expires-in}")
    private Integer refreshTokenExpiresIn;

    @Autowired
    public RefreshTokenService(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, AppUserRepository appUserRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.appUserRepository = appUserRepository;
    }


    public AuthenticationResponse refreshToken(UUID token) {

        // verify whether the provided refresh token is present in the db
        RefreshToken refreshToken = refreshTokenRepository.findById(token)
                .orElseThrow(() ->
                        new InvalidConfirmationTokenException("Refresh token not found"));

        // extract the user since it is needed in order to generate refresh token, and verify whether the user associated
        // with the provided refresh token still exists
        AppUser appUser = appUserRepository.findByEmail(
                refreshToken.getAppUser().getUsername()).orElseThrow(() ->
                new InvalidConfirmationTokenException("There is no user associated with this refresh token"));


        // verify whether refresh token has expired
        if (LocalDateTime.now().isAfter(refreshToken.getExpiresAt())) {
            throw new InvalidConfirmationTokenException("Token has expired");
        }

        String accessToken = jwtUtil.generateAccessToken(appUser);

        return new AuthenticationResponse(
                accessToken,
                accessTokenExpiresIn,
                refreshTokenExpiresIn,
                token
        );
    }
}
