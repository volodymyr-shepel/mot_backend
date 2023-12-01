package com.mot.refreshToken;

import com.mot.appUser.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue
    private Long id;

    // represents the token which will be included as path variable in confirmation link
    @NotNull
    private String token;

    // The user may have multiple confirmation tokens associated with them in case they cannot
    // confirm it in time or do not receive the email.
    @NotNull
    @ManyToOne
    private AppUser appUser;

    public RefreshToken(Long id, String token, AppUser appUser) {
        this.id = id;
        this.token = token;
        this.appUser = appUser;
    }
    public RefreshToken(){

    }

    public RefreshToken(String token, AppUser appUser) {
        this.token = token;
        this.appUser = appUser;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
