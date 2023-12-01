package com.mot.exceptions;


public class JwtValidationException extends RuntimeException{
    public JwtValidationException(String customMessage) {
        super(customMessage);
    }
}

