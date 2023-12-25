package com.mot.repository;

import com.mot.exception.InvalidTokenException;
import com.mot.model.token.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByToken(String token);

    default VerificationToken getVerificationTokenOrThrowAnException(String token) {
        return findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Verification token not found"));
    }

    boolean existsByAppUserEmailAndExpiresAtAfterAndConfirmedAtIsNull(String email, LocalDateTime now);

}
