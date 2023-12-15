package com.mot.model.token;




import com.mot.exception.InvalidTokenException;
import com.mot.model.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.time.LocalDateTime;


@Getter
@Entity
public class VerificationToken extends Token{


    @NotNull
    private String token;

    @Column
    private LocalDateTime confirmedAt;

    public VerificationToken() {

    }

    public VerificationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        super(createdAt,expiresAt,appUser);
        this.token = token;

    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void validateToken(){
        this.validateIfTokenAlreadySubmitted();
        this.validateTokenExpiration();
    }
    private void validateIfTokenAlreadySubmitted() {
        if (this.getConfirmedAt() != null) {
            throw new InvalidTokenException("Verification token was already submitted");
        }
    }
}
