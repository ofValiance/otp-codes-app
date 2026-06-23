package dev.otpcodesapp.api.dto.request;


public record LoginRequest(
        String login,
        String password
) {}
