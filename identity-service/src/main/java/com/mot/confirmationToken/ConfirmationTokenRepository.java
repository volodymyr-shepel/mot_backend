package com.mot.confirmationToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    Optional<ConfirmationToken> findByToken(String token);

    boolean existsByAppUserEmailAndExpiresAtAfter(String email, LocalDateTime now);
}
