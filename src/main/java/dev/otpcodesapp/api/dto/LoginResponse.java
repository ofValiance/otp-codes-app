package dev.otpcodesapp.api.dto;


public record LoginResponse(
        String login,
        String token
) {}
