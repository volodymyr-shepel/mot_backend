package com.mot.service.registration;

import com.mot.dtos.AppUserDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface RegistrationService {
    ResponseEntity<UUID> signUp(AppUserDTO appUserDTO);
}
