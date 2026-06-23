package dev.otpcodesapp.api.dto;


public record RegisterRequest(
        String login,
        String password,
        String role
) {}
