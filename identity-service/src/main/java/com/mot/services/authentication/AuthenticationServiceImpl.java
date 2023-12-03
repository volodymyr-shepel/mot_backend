package com.mot.services.authentication;
import com.mot.amqp.RabbitMQMessageProducer;
import com.mot.appUser.AppUser;
import com.mot.appUser.AppUserRepository;
import com.mot.confirmationToken.ConfirmationTokenRepository;
import com.mot.confirmationToken.ConfirmationTokenService;
import com.mot.dtos.AuthenticationRequest;
import com.mot.dtos.AuthenticationResponse;
import com.mot.dtos.NotificationDTO;
import com.mot.dtos.ResendVerificationEmailRequest;
import com.mot.exceptions.InvalidTokenException;
import com.mot.exceptions.UserNotFoundException;
import com.mot.refreshToken.RefreshToken;
import com.mot.refreshToken.RefreshTokenRepository;
import com.mot.util.JwtUtil;
import com.mot.util.NotificationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    private final ConfirmationTokenService confirmationTokenService;

    private final AppUserRepository appUserRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Value("${app.security.jwt.expires-in}")
    private long accessTokenExpiresIn; // the value is provided in seconds

    @Value("${app.security.jwt.refresh-token.expires-in}")
    private long refreshTokenExpiresIn; // the value is provided in seconds

    @Value("${app.config.verification-email-subject}")
    private String verificationEmailSubject;

    @Value("${app.rabbitmq.internal-exchange}")
    private String internalExchange;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    @Value("${app.base-url}")
    private String baseUrl;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil, ConfirmationTokenService confirmationTokenService, AppUserRepository appUserRepository, RabbitMQMessageProducer rabbitMQMessageProducer, ConfirmationTokenRepository confirmationTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.confirmationTokenService = confirmationTokenService;
        this.appUserRepository = appUserRepository;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
        this.confirmationTokenRepository = confirmationTokenRepository;
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


        UUID refreshToken = saveRefreshToken(user);

        AuthenticationResponse response = buildAuthenticationResponse(accessToken,refreshToken);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<String> resendVerificationEmail(ResendVerificationEmailRequest resendVerificationEmailRequest){
        // the email for which the verification email should be resent
        String email = resendVerificationEmailRequest.email();

        AppUser appUser = appUserRepository.findByEmail(
                email).orElseThrow(() ->
                new UserNotFoundException("There is no such user"));

        if(appUser.isEnabled()){
            throw new IllegalStateException("Provided account is already activated");
        }
        if(confirmationTokenRepository.existsByAppUserEmailAndExpiresAtAfter(email,LocalDateTime.now())){
            throw new IllegalStateException("There is active verification email associated with the provided account." +
                    " Verify it or try again in 15 minutes");
        }

        NotificationDTO notification = new NotificationDTO(
                appUser.getUsername(),
                verificationEmailSubject,
                NotificationType.EMAIL_VERIFICATION,
                generateVerificationEmailFields(appUser)
        );
        rabbitMQMessageProducer.publish(
                notification,
                internalExchange,
                emailRoutingKey
        );
        return ResponseEntity.ok("Verification email was sent successfully");


    }
    private HashMap<String,String> generateVerificationEmailFields(AppUser appUser){
        String token = confirmationTokenService.generateConfirmationToken(appUser);
        String confirmationLink = generateConfirmationLink(token);

        return new HashMap<>() {{
            // there is a placeholder in html template which represents the confirmation link
            put("link", confirmationLink);
            // there is a placeholder in html template which represents the name of the user
            put("name", appUser.getFirstName());
        }};
    }

    private String generateConfirmationLink(String token) {
        return  baseUrl + "/api/auth/confirm?token=" + token;
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
                LocalDateTime.now().plusSeconds(refreshTokenExpiresIn),
                user
        );
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return savedToken.getId();


    }
}
