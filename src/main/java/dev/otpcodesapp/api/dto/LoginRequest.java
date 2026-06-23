package dev.otpcodesapp.api.dto;


public record LoginRequest(
        String login,
        String password
) {}
