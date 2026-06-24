package dev.otpcodesapp.model;

import java.time.Instant;


public record Code(
        Long id,
        Long userId,
        Long operationId,
        int code,
        Status status,
        Instant createdAt,
        Instant expiresAt,
        Instant usedAt
) {
    public enum Status {
        ACTIVE,
        USED,
        EXPIRED
    }
}
