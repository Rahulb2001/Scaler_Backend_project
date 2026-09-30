package com.scaler.backend.userauth.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.backend.userauth.dtos.EmailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducerClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${userauth.kafka.signup-topic:signup}")
    private String signupTopic;

    public KafkaProducerClient(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSignupEvent(EmailDto emailDto) {
        try {
            String payload = objectMapper.writeValueAsString(emailDto);
            kafkaTemplate.send(signupTopic, payload);
        } catch (JsonProcessingException e) {
            // A malformed signup email is not worth failing the signup request over -
            // the user account is already created at this point.
            log.error("Could not serialize signup email event for {}", emailDto.getTo(), e);
        }
    }
}
