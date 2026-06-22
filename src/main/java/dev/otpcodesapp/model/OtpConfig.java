package dev.otpcodesapp.model;

public record OtpConfig(
        long id,
        int codeLength,
        int ttlSeconds
) {}
