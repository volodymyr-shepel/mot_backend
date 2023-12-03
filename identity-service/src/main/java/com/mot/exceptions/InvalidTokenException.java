package com.mot.exceptions;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String customMessage) {
        super(customMessage);
    }
}

