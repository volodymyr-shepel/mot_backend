package com.mot.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationRequest(
        @JsonProperty("email")
        String email,
        @JsonProperty("password")
        String password
) {
}
