package com.mot.refreshToken;

import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.confirmationToken.ConfirmationToken;
import com.mot.dtos.AuthenticationResponse;
import com.mot.dtos.RefreshTokenRequest;
import com.mot.exceptions.InvalidConfirmationTokenException;
import com.mot.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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


    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        final String token;
        final String userEmail;

        token = refreshTokenRequest.refreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new InvalidConfirmationTokenException("Refresh token not found"));

        AppUser appUser = appUserRepository.findByEmail(
                refreshToken.getAppUser().getUsername()).orElseThrow(() ->
                new InvalidConfirmationTokenException("There is no user associated with this refresh token"));


        if (jwtUtil.isTokenExpired(token)) {
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
