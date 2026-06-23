package dev.otpcodesapp.api.exception;

public class AdminAlreadyExistsException extends BusinessException {
    public AdminAlreadyExistsException(String message) {
        super(message);
    }
}
