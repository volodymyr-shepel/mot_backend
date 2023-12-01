package com.mot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthenticationResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        Integer expiresIn,
        @JsonProperty("refresh_expires_in")
        Integer refreshExpiresIn,

        @JsonProperty("refresh_token")
        UUID refreshToken
) {
}
