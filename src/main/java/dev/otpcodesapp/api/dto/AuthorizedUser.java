package dev.otpcodesapp.api.dto;


public record AuthorizedUser(
        String login,
        String role
) {}
