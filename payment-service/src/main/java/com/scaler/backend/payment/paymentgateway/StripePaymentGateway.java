package com.scaler.backend.payment.paymentgateway;

import com.scaler.backend.payment.dtos.PaymentRequestDto;
import com.scaler.backend.payment.exceptions.PaymentGatewayException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentGateway implements IPaymentGateway {

    @Override
    public String getName() {
        return "STRIPE";
    }

    @Override
    public String generatePaymentLink(PaymentRequestDto request) {
        try {
            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setCurrency("usd")
                    // Stripe wants the amount in the smallest currency unit (cents for USD).
                    .setUnitAmount(Math.round(request.getAmount() * 100))
                    .setProductData(
                            PriceCreateParams.ProductData.builder()
                                    .setName("Order " + request.getOrderId())
                                    .build()
                    )
                    .build();
            Price price = Price.create(priceParams);

            PaymentLinkCreateParams linkParams = PaymentLinkCreateParams.builder()
                    .addLineItem(
                            PaymentLinkCreateParams.LineItem.builder()
                                    .setPrice(price.getId())
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();
            PaymentLink paymentLink = PaymentLink.create(linkParams);

            return paymentLink.getUrl();
        } catch (StripeException e) {
            throw new PaymentGatewayException("Could not create a Stripe payment link: " + e.getMessage(), e);
        }
    }
}
