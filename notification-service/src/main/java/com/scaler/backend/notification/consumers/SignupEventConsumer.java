package com.scaler.backend.notification.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.backend.notification.dtos.EmailDto;
import com.scaler.backend.notification.mail.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SignupEventConsumer {

    private final EmailSender emailSender;
    private final ObjectMapper objectMapper;

    public SignupEventConsumer(EmailSender emailSender, ObjectMapper objectMapper) {
        this.emailSender = emailSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${notification.kafka.signup-topic:signup}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void onSignup(String message) {
        try {
            EmailDto emailDto = objectMapper.readValue(message, EmailDto.class);
            emailSender.send(emailDto.getTo(), emailDto.getSubject(), emailDto.getBody());
        } catch (Exception e) {
            log.error("Could not process signup event: {}", message, e);
        }
    }
}
