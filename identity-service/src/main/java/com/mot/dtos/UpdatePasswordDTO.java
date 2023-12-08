package com.mot.dtos;

public record UpdatePasswordDTO(
        String password,
        String verificationToken
) {

}
