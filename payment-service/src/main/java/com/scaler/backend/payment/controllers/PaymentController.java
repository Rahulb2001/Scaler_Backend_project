package com.scaler.backend.payment.controllers;

import com.scaler.backend.payment.dtos.PaymentRequestDto;
import com.scaler.backend.payment.dtos.PaymentResponseDto;
import com.scaler.backend.payment.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDto> generatePaymentLink(@RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(paymentService.generatePaymentLink(request));
    }
}
