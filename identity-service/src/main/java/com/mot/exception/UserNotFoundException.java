package com.mot.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String customMessage) {
        super(customMessage);
    }
}
