package com.mot.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

// Template for the exception which will be returned by exception handler
public record ApiException(String message,
                           HttpStatus httpStatus,
                           ZonedDateTime timestamp) {

}
