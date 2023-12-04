package com.mot.services.authentication;
import com.mot.appUser.AppUser;
import com.mot.dtos.UserAuthenticationRequest;
import com.mot.dtos.UserAuthenticationResponse;
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

        //extracts the authenticated user's information from the Authentication object's principal
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

//    @Transactional
//    public ResponseEntity<String> verifyEmailForResend(EmailAddressDTO emailAddressDTO){
//        // the email for which the verification email should be resent
//
//
//
//
//        return ResponseEntity.ok("Verification email was sent successfully");
//
//
//    }
//    private HashMap<String,String> generateVerificationEmailFields(AppUser appUser){
//        String token = confirmationTokenService.generateConfirmationToken(appUser);
//        String confirmationLink = generateConfirmationLink(token);
//
//        return new HashMap<>() {{
//            // there is a placeholder in html template which represents the confirmation link
//            put("link", confirmationLink);
//            // there is a placeholder in html template which represents the name of the user
//            put("name", appUser.getFirstName());
//        }};
//    }
//
//    private String generateConfirmationLink(String token) {
//        return  baseUrl + "/api/auth/confirm?token=" + token;
//    }


}
