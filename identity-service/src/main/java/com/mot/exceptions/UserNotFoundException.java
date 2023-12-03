package com.mot.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String customMessage) {
        super(customMessage);
    }
}
