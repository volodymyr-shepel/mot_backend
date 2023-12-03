package com.mot.refreshToken;

import com.mot.dtos.AuthenticationResponse;

import java.util.UUID;

public interface RefreshTokenService {
    AuthenticationResponse refreshToken(UUID token);
}
