package com.mot.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

//  Exception Handler to manage exceptions and return them in the appropriate format
@ControllerAdvice
public class IdentityExceptionHandler {
    @ExceptionHandler(value = {
            PasswordValidationException.class,
            InvalidTokenException.class,
            UsernameNotFoundException.class,
            IllegalStateException.class


    })
    public ResponseEntity<Object> handleCustomExceptions(RuntimeException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(

                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
        );

        return new ResponseEntity<>(apiException, badRequest);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        // Handle ConstraintViolationException for validation errors
        String message = "Validation failed. List of constraint violations: " +
                e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));

        ApiException apiException = new ApiException(
                message,
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
        );

        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    // used to handle PSQL exception such unique constraint violation
    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<Object> handlePSQLException(PSQLException e) {
        String errorMessage;

        if (e.getMessage().contains("duplicate key value violates unique constraint")) {
            errorMessage = "User with provided email already exists. Please use another email";
        } else {
            errorMessage = e.getMessage();
        }

        ApiException apiException = new ApiException(
                errorMessage,
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
        );

        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}