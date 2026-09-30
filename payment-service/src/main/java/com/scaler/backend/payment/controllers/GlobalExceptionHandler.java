package com.scaler.backend.payment.controllers;

import com.scaler.backend.payment.dtos.ErrorResponseDto;
import com.scaler.backend.payment.exceptions.PaymentGatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentGatewayException(PaymentGatewayException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("Something went wrong: " + e.getMessage()));
    }
}
