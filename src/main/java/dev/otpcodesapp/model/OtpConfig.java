package dev.otpcodesapp.model;


public record OtpConfig(
        Long id,
        int codeLength,
        int ttlSeconds
) {}
