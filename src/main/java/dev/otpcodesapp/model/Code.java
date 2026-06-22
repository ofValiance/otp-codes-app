package dev.otpcodesapp.model;

import java.time.Instant;

public record Code(
        long id,
        long userId,
        long operationId,
        String codeHash,
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
