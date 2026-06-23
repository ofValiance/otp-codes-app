package dev.otpcodesapp.api.exception;

public class ExpiredCodeException extends BusinessException {
    public ExpiredCodeException(String message) {
        super(message);
    }
}
