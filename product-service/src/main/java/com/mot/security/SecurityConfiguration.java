package com.mot.security;


import com.mot.util.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {



    private final JwtTokenFilter jwtTokenFilter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfiguration(JwtTokenFilter jwtTokenFilter,
                                 JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/product/admin/**").hasAuthority(UserRole.ADMIN.name())
                        .requestMatchers("/api/product/customer/**").hasAuthority(UserRole.CUSTOMER.name())
                        .requestMatchers("/api/product/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll);
        http.exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint));
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }







}