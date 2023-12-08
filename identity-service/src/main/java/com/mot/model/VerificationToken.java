package com.mot.model;




import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Builder
public class VerificationToken {
    @Id
    @GeneratedValue
    private UUID id;

    // represents the token which will be included as path variable in confirmation link
    @NotNull
    private String token;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime confirmedAt;

    // The user may have multiple confirmation tokens associated with them in case they cannot
    // confirm it in time or do not receive the email.
    @NotNull
    @ManyToOne
    private AppUser appUser;

    public VerificationToken(UUID id,
                             String token,
                             LocalDateTime createdAt,
                             LocalDateTime expiresAt,
                             LocalDateTime confirmedAt,
                             AppUser appUser) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.confirmedAt = confirmedAt;
        this.appUser = appUser;
    }

    public VerificationToken() {

    }

    public VerificationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
