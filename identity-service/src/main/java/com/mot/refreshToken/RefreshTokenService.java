package com.mot.refreshToken;

import com.mot.dtos.UserAuthenticationResponse;

import java.util.UUID;

public interface RefreshTokenService {
    UserAuthenticationResponse refreshToken(UUID token);
}
