package com.mot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.UUID;


public record AuthenticationResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        long expiresIn,
        @JsonProperty("refresh_expires_in")
        long refreshExpiresIn,

        @JsonProperty("refresh_token")
        UUID refreshToken
) {
}
