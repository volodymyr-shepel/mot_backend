package com.mot.exception;


public class JwtValidationException extends RuntimeException{
    public JwtValidationException(String customMessage) {
        super(customMessage);
    }
}

