package com.scaler.backend.userauth.controllers;

import com.scaler.backend.userauth.dtos.ErrorResponseDto;
import com.scaler.backend.userauth.exceptions.IncorrectPasswordException;
import com.scaler.backend.userauth.exceptions.InvalidTokenException;
import com.scaler.backend.userauth.exceptions.UserAlreadyExistsException;
import com.scaler.backend.userauth.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponseDto> handleIncorrectPassword(IncorrectPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("Something went wrong: " + e.getMessage()));
    }
}
