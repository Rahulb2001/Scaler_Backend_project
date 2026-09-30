package com.scaler.backend.payment.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private double amount;
    private String orderId;
    private String name;
    private String email;
    private String phoneNumber;

    // Optional - "RAZORPAY" or "STRIPE". Left blank, PaymentGatewayChooserStrategy
    // falls back to a sensible default instead of rejecting the request.
    private String preferredGateway;
}
