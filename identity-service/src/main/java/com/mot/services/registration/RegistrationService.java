package com.mot.services.registration;

import com.mot.dtos.AppUserDTO;
import org.springframework.http.ResponseEntity;

public interface RegistrationService {
    ResponseEntity<Integer> register(AppUserDTO appUserDTO);
}
