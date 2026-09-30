package com.scaler.backend.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponseDto {
    private String paymentLink;
    private String gateway;
}
