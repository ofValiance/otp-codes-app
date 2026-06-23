package dev.otpcodesapp.api.dto.request;


public record ValidateRequest(
        Long operationId,
        String code
) {}
