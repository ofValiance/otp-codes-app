package dev.otpcodesapp.api.dto.response;


public record LoginResponse(
        String login,
        String token
) {}
