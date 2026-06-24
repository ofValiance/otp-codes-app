package dev.otpcodesapp.api.exception;

public class ConfigNotFoundException extends BusinessException {
    public ConfigNotFoundException(String message) {
        super(message);
    }
}
