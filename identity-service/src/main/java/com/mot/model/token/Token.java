package com.mot.model.token;

import com.mot.exception.InvalidTokenException;
import com.mot.model.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class Token {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime expiresAt;

    @NotNull
    @ManyToOne
    private AppUser appUser;

    public Token() {
    }

    public Token(UUID id, LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        this.id = id;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }

    public Token(LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }

    public void validateTokenExpiration() {
        if (LocalDateTime.now().isAfter(this.expiresAt)) {
            throw new InvalidTokenException("Token has expired");
        }
    }


    public String extractEmailFromToken() {
        return this.appUser.getUsername();
    }


}

