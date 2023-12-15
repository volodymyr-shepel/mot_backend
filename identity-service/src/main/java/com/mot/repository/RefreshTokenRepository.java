package com.mot.repository;

import com.mot.exception.InvalidTokenException;
import com.mot.model.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,UUID> {

    default RefreshToken getRefreshTokenByIdOrThrowAnException(UUID token) {
        return findById(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
    }


}

