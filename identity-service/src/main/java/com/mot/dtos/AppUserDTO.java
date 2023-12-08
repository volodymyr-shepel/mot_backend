package com.mot.dtos;

import com.mot.enums.UserRole;
import lombok.Builder;

public record AppUserDTO(String email,
                         String firstName,
                         String lastName,
                         String password,
                         UserRole userRole) {
}

