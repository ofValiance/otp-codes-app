package dev.otpcodesapp.api.dto.request;


public record GenerateRequest(
        Long operationId,
        String channel,
        String destination
) {}
