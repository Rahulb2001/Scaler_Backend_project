package com.scaler.backend.payment.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * An earlier version of this endpoint just printed whatever Stripe sent it,
 * with no signature check - meaning anyone who found the URL could post a
 * fake "payment succeeded" event. This verifies the signature against our
 * webhook secret before trusting the payload at all.
 */
@Slf4j
@RestController
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/stripeWebhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String signatureHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Rejected a Stripe webhook call with an invalid signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> log.info("Checkout session completed: {}", event.getId());
            case "payment_intent.succeeded" -> log.info("Payment succeeded: {}", event.getId());
            case "payment_intent.payment_failed" -> log.info("Payment failed: {}", event.getId());
            default -> log.info("Unhandled Stripe event type: {}", event.getType());
        }

        return ResponseEntity.ok("received");
    }
}
