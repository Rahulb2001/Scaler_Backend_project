package com.scaler.backend.payment.paymentgateway;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.scaler.backend.payment.dtos.PaymentRequestDto;
import com.scaler.backend.payment.exceptions.PaymentGatewayException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class RazorpayPaymentGateway implements IPaymentGateway {

    private final RazorpayClient razorpayClient;

    public RazorpayPaymentGateway(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    @Override
    public String getName() {
        return "RAZORPAY";
    }

    @Override
    public String generatePaymentLink(PaymentRequestDto request) {
        try {
            JSONObject customer = new JSONObject();
            customer.put("name", request.getName());
            customer.put("email", request.getEmail());
            customer.put("contact", request.getPhoneNumber());

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);

            JSONObject paymentLinkRequest = new JSONObject();
            // Razorpay wants the amount in paise, not rupees.
            paymentLinkRequest.put("amount", Math.round(request.getAmount() * 100));
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("accept_partial", false);
            paymentLinkRequest.put("reference_id", request.getOrderId());
            paymentLinkRequest.put("customer", customer);
            paymentLinkRequest.put("notify", notify);
            paymentLinkRequest.put("callback_url", "https://example.com/payment-callback");
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);
            return paymentLink.get("short_url");
        } catch (RazorpayException e) {
            throw new PaymentGatewayException("Could not create a Razorpay payment link: " + e.getMessage(), e);
        }
    }
}
