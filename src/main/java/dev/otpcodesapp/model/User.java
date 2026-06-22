package dev.otpcodesapp.model;

public record User(
        long id,
        String login,
        String passwordHash,
        Role role
) {
    public enum Role {
        ADMIN,
        USER
    }
}
