package com.mot.security;

import com.mot.appUser.UserRole;
import com.mot.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;


    @Autowired
    public JwtTokenFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = JwtUtil.extractAuthToken(authHeader);

        //jwtUtil.validateToken(accessToken);

        // TODO: Try to modify so it does not log error messages to the console if token verification fails??
        // problem with throwing an exception inside filter
        jwtUtil.validateToken(accessToken);


        // Extract roles from claims
//        if(!jwtUtil.checkUserRole(accessToken, UserRole.ADMIN)){
//            filterChain.doFilter(request,response);
//        };

        setAuthenticationContext(accessToken,request);

        filterChain.doFilter(request,response);

    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        final String userEmail = jwtUtil.extractUsername(token);

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(userEmail, null, jwtUtil.extractAuthoritiesFromToken(token));

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }



}
