package com.mot.security;

import com.mot.appUser.AppUserDetailsService;
import com.mot.appUser.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public SecurityConfiguration(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable);
        http

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().permitAll()

                )
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll);
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }


    // Authentication manager is responsible for performing authentication.It's responsible for verifying the identity
    // of a user by validating their credentials (e.g., username and password)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // The AuthenticationManager typically delegates the authentication process to one or more AuthenticationProvider
    // implementations
    @Bean
    public AuthenticationProvider authenticationProvider(){
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // configures the AuthenticationProvider to use a specific UserDetailsService implementation to retrieve
        // user details during the authentication process.
        authenticationProvider.setUserDetailsService(userDetailsService());

        //specifies the password encoder that should be used by the AuthenticationProvider to verify the encoded
        // passwords stored in the database during the authentication process.

        authenticationProvider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder());
        return authenticationProvider;

    }

    // The UserDetailsService interface is used to retrieve user-specific data during the authentication process
    @Bean
    public UserDetailsService userDetailsService() {
        return new AppUserDetailsService(appUserRepository);

    }



}