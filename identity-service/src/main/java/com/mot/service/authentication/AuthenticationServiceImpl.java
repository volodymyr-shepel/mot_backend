package com.mot.service.authentication;
import com.mot.model.AppUser;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
import com.mot.model.RefreshToken;
import com.mot.repository.RefreshTokenRepository;
import com.mot.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.security.jwt.expires-in}")
    private long accessTokenExpiresIn; // the value is provided in seconds

    @Value("${app.security.jwt.refresh-token.expires-in}")
    private long refreshTokenExpiresIn; // the value is provided in seconds

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     RefreshTokenRepository refreshTokenRepository,
                                     JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public ResponseEntity<UserAuthenticationResponse> authenticate(UserAuthenticationRequest userAuthenticationRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userAuthenticationRequest.email(), userAuthenticationRequest.password())
        );

        AppUser user = (AppUser) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(user);

        UUID refreshToken = saveRefreshToken(user);

        UserAuthenticationResponse response = buildAuthenticationResponse(accessToken,refreshToken);
        return ResponseEntity.ok(response);
    }

    public UserAuthenticationResponse buildAuthenticationResponse(String accessToken, UUID refreshToken){
        return new UserAuthenticationResponse(
                accessToken,
                accessTokenExpiresIn,
                refreshTokenExpiresIn,
                refreshToken
        );

    }
    private UUID saveRefreshToken(AppUser user){
        RefreshToken refreshToken = new RefreshToken(
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(refreshTokenExpiresIn),
                user
        );

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return savedToken.getId();
    }

}
