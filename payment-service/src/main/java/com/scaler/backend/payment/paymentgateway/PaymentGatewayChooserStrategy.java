package com.scaler.backend.payment.paymentgateway;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Picks a gateway by name (case-insensitive) if the caller asked for one and
 * we actually have it, otherwise falls back to Stripe. An earlier version of
 * this class always returned Stripe regardless of what was requested - this
 * is the fixed version that actually looks at PaymentRequestDto.preferredGateway.
 */
@Component
public class PaymentGatewayChooserStrategy {

    private static final String DEFAULT_GATEWAY = "STRIPE";

    private final Map<String, IPaymentGateway> gatewaysByName;

    public PaymentGatewayChooserStrategy(List<IPaymentGateway> gateways) {
        this.gatewaysByName = gateways.stream()
                .collect(Collectors.toMap(g -> g.getName().toUpperCase(), Function.identity()));
    }

    public IPaymentGateway choose(String preferredGateway) {
        if (preferredGateway != null && gatewaysByName.containsKey(preferredGateway.toUpperCase())) {
            return gatewaysByName.get(preferredGateway.toUpperCase());
        }
        return gatewaysByName.getOrDefault(DEFAULT_GATEWAY, gatewaysByName.values().iterator().next());
    }
}
