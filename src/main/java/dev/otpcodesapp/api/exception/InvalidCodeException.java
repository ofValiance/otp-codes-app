package dev.otpcodesapp.api.exception;

public class InvalidCodeException extends BusinessException {
    public InvalidCodeException(String message) {
        super(message);
    }
}
