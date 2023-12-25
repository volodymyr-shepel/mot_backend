package com.mot.service.authentication;
import com.mot.dtos.CredentialsDTO;
import com.mot.model.AppUser;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
import com.mot.model.token.RefreshToken;
import com.mot.repository.AppUserRepository;
import com.mot.repository.RefreshTokenRepository;
import com.mot.util.jwt.JwtUtil;
import com.mot.util.passwordValidator.PasswordValidator;
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

    private final AppUserRepository appUserRepository;

    private final PasswordValidator passwordValidator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.security.jwt.expires-in}")
    private long accessTokenExpiresIn; // the value is provided in seconds

    @Value("${app.security.jwt.refresh-token.expires-in}")
    private long refreshTokenExpiresIn; // the value is provided in seconds

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                                     AppUserRepository appUserRepository, PasswordValidator passwordValidator, RefreshTokenRepository refreshTokenRepository,
                                     JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.passwordValidator = passwordValidator;
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

    @Override
    public ResponseEntity<String> validateCredentials(CredentialsDTO credentialsDTO) {
        String email = credentialsDTO.email(); // Assuming you have a getEmail() method in your CredentialsDTO
        String password = credentialsDTO.password();





        if (appUserRepository.existsByEmail(email)) {
            // User with the given email already exists, return Bad Request
            return ResponseEntity.badRequest().body("User with this email already exists");
        }

        // If the user doesn't exist, continue with password validation
        passwordValidator.validatePassword(password);

        return ResponseEntity.ok("Credentials verified");
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
