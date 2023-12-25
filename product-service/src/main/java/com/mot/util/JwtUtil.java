package com.mot.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${app.security.jwt.secret}")
    private String SECRET_KEY;


    @Value("${app.security.jwt.issuer}")
    private String issuer;


    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public Boolean validateToken(String token){
        try{
            // Parse and validatePassword the token
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            // If no exceptions are thrown, the token is valid
            return true;
        }
        catch(Exception e){
            return false;
        }


    }
    public List<GrantedAuthority> extractAuthoritiesFromToken(String authHeader) {
        List<String> userRoles = extractRolesFromToken(authHeader);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : userRoles) {
            // Assuming your UserRole class has a method to get the role name
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
    public List<String> extractRolesFromToken(String authHeader) {
        Claims claims = extractAllClaims(authHeader);
        return (List<String>) claims.get("roles");
    }

    public static String extractAuthToken(String authHeader) {

        if (authHeader.startsWith("Bearer ")) {
            // Extract the JWT token
            return authHeader.substring(7);
        }
        return null;

    }


}
