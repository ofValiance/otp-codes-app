package dev.otpcodesapp.api.exception;

public class CodeDoesNotExistException extends BusinessException {
    public CodeDoesNotExistException(String message) {
        super(message);
    }
}
