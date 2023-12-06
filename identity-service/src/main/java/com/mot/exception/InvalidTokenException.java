package com.mot.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String customMessage) {
        super(customMessage);
    }
}

