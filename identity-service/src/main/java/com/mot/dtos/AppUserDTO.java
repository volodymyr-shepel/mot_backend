package com.mot.dtos;

import com.mot.appUser.UserRole;

public record AppUserDTO(String email,
                         String firstName,
                         String lastName,
                         String password,
                         UserRole userRole) {
}

