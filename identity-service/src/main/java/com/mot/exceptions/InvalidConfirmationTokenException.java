package com.mot.exceptions;

public class InvalidConfirmationTokenException extends RuntimeException{
    public InvalidConfirmationTokenException(String customMessage) {
        super(customMessage);
    }
}

