package com.mot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;



//The JwtAuthenticationEntryPoint class is an implementation of Spring Security's AuthenticationEntryPoint
//interface. It is used to handle and customize the behavior when an unauthenticated user tries to access
//        a secured resource.

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String errorMessage = "You do not have the required permissions to access this resource.";

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Include the error message in the JSON response
        String jsonResponse = String.format("{ \"error\": \"Unauthorized\", \"message\": \"%s\" }", errorMessage);
        response.getWriter().write(jsonResponse);
    }
}
