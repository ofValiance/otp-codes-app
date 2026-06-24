package dev.otpcodesapp.api.dto.request;

public record UpdateOtpConfigRequest(
        int codeLength,
        int ttlSeconds
) {}
