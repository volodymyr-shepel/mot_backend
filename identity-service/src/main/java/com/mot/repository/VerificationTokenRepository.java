package com.mot.repository;

import com.mot.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByToken(String token);

    boolean existsByAppUserEmailAndExpiresAtAfter(String email, LocalDateTime now);
}
