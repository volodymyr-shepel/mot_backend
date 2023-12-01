package com.mot.refreshToken;

import com.mot.appUser.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue
    private UUID id;

    // represents the token which will be included as path variable in confirmation link
    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime expiresAt;


    @NotNull
    @ManyToOne
    private AppUser appUser;

    public RefreshToken(LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }

    public RefreshToken(UUID id, LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        this.id = id;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }

    public RefreshToken(){

    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
