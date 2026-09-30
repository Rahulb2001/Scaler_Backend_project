package com.scaler.backend.payment.paymentgateway;

import com.scaler.backend.payment.dtos.PaymentRequestDto;

public interface IPaymentGateway {

    String getName();

    String generatePaymentLink(PaymentRequestDto request);
}
