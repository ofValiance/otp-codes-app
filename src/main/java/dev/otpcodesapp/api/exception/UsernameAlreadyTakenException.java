package dev.otpcodesapp.api.exception;

public class UsernameAlreadyTakenException extends BusinessException {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
