package dev.otpcodesapp.api.dto;


public record AuthorizedUser(
        Long id,
        String login,
        String role
) {}
