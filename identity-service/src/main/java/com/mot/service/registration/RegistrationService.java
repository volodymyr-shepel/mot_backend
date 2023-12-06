package com.mot.service.registration;

import com.mot.dtos.AppUserDTO;
import org.springframework.http.ResponseEntity;

public interface RegistrationService {
    ResponseEntity<Integer> register(AppUserDTO appUserDTO);
}
