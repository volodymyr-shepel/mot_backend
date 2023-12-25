package com.mot.repository;

import com.mot.exception.UserNotFoundException;
import com.mot.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Integer> {

    default AppUser getAppUserByEmailOrThrowUserNotFoundException(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
