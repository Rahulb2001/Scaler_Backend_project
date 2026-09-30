package com.scaler.backend.payment.services;

import com.scaler.backend.payment.dtos.PaymentRequestDto;
import com.scaler.backend.payment.dtos.PaymentResponseDto;
import com.scaler.backend.payment.paymentgateway.IPaymentGateway;
import com.scaler.backend.payment.paymentgateway.PaymentGatewayChooserStrategy;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentGatewayChooserStrategy chooserStrategy;

    public PaymentService(PaymentGatewayChooserStrategy chooserStrategy) {
        this.chooserStrategy = chooserStrategy;
    }

    public PaymentResponseDto generatePaymentLink(PaymentRequestDto request) {
        IPaymentGateway gateway = chooserStrategy.choose(request.getPreferredGateway());
        String link = gateway.generatePaymentLink(request);
        return new PaymentResponseDto(link, gateway.getName());
    }
}
