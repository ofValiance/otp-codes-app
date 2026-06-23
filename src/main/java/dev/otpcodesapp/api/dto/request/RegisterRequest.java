package dev.otpcodesapp.api.dto.request;


public record RegisterRequest(
        String login,
        String password,
        String role
) {}
