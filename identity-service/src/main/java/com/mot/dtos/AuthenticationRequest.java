package com.mot.dtos;

public record AuthenticationRequest(
        String email,
        String password
) {
}
