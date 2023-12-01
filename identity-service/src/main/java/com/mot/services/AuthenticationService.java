package com.mot.services;
import com.mot.appUser.AppUser;
import com.mot.dtos.AuthenticationRequest;
import com.mot.dtos.AuthenticationResponse;
import com.mot.refreshToken.RefreshToken;
import com.mot.refreshToken.RefreshTokenRepository;
import com.mot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.security.jwt.expires-in}")
    private Integer accessTokenExpiresIn;

    @Value("${app.security.jwt.refresh-token.expires-in}")
    private Integer refreshTokenExpiresIn;


    public AuthenticationService(AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(), authenticationRequest.password())
        );
        //extracts the authenticated user's information from the Authentication object's principal
        AppUser user = (AppUser) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(user);

        // Refresh token is in the form of jwt
        //String refreshToken = jwtUtil.generateRefreshToken(user);

        UUID refreshToken = saveRefreshToken(user);

        AuthenticationResponse response = buildAuthenticationResponse(accessToken,refreshToken);
        return ResponseEntity.ok(response);
    }

    private AuthenticationResponse buildAuthenticationResponse(String accessToken,UUID refreshToken){
        return new AuthenticationResponse(
                accessToken,
                accessTokenExpiresIn,
                refreshTokenExpiresIn,
                refreshToken

        );

    }

    private UUID saveRefreshToken(AppUser user){
        RefreshToken refreshToken = new RefreshToken(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                user
        );
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return savedToken.getId();


    }
}
