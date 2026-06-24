package dev.otpcodesapp.api.dto.response;

public record GetOtpConfigResponse(
        int codeLength,
        int ttlSeconds
) {}
