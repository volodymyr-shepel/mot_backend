package com.mot.model.token;

import com.mot.model.AppUser;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;


@Entity
@Builder
public class RefreshToken extends Token{
    public RefreshToken() {

    }
    public RefreshToken(LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        super(createdAt, expiresAt, appUser);
    }
}
