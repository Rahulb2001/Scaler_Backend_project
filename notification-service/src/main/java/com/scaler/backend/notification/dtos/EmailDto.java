package com.scaler.backend.notification.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mirrors the EmailDto published by user-authentication-service. The two
 * services don't share a library, so this is intentionally kept as a
 * duplicate, plain-Jackson-compatible copy rather than a shared dependency.
 */
@Getter
@Setter
@NoArgsConstructor
public class EmailDto {
    private String to;
    private String subject;
    private String body;
}
