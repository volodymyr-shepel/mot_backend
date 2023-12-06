package com.mot.util.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    @Value("${app.security.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.security.jwt.expires-in}")
    private Long accessTokenExpiresIn;

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Extract user roles from UserDetails authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        if (authorities != null && !authorities.isEmpty()) {
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            claims.put("roles", roles);
        }

        return buildToken(claims, userDetails,accessTokenExpiresIn);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails,Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // multiply by 1000 in order to change seconds to milliseconds
                .setExpiration(new Date(System.currentTimeMillis() + (expiration * 1000)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

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
    public  Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
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
