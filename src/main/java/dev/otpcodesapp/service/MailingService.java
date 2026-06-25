package dev.otpcodesapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.otpcodesapp.api.exception.BusinessException;
import dev.otpcodesapp.api.exception.InvalidChannelOrDestinationException;
import dev.otpcodesapp.config.EnvManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;


public class MailingService {

    public enum Channel{
        SMS,
        EMAIL,
        TELEGRAM,
        FILE
    }

    private static final Logger logger = LoggerFactory.getLogger(MailingService.class);

    public void sendCode(String strChannel, String destination, int code) {
        logger.info("Attempting to send code via channel: {}, destination: {}", strChannel, destination);
        try {
            Channel channel = Channel.valueOf(strChannel);
            switch (channel) {
                case SMS:
                    break;
                case EMAIL:
                    break;
                case TELEGRAM:
                    break;
                case FILE:
                    logger.info("Saving code to file at destination: {}", destination);
                    saveFile(destination, code);
                    break;
            }
            logger.info("Code successfully processed for channel: {}", strChannel);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid channel type provided: {}", strChannel);
            throw new InvalidChannelOrDestinationException("There is no " + strChannel + " channel type");
        }
    }

    private void sendSms() {

    }

    private void sendEmail() {

    }

    private void sendTelegram() {

    }

    private void saveFile(String strPath, int code) {
        logger.debug("Saving code to file: {}", strPath);
        try {
            Path path = Path.of(strPath);
            Path basePath = Path.of(EnvManager.getString("OUTPUT_PATH"));
            Path outputPath = basePath.resolve(path);
            Path parentDir = outputPath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.writeString(outputPath, String.valueOf(code));
            logger.info("Code successfully saved to file: {}", outputPath);
        } catch (InvalidPathException e) {
            logger.error("Invalid path provided for file saving: {}", strPath, e);
            throw new InvalidChannelOrDestinationException("Path " + strPath + " is invalid");
        } catch (IOException e) {
            logger.error("Error writing code to file: {}", strPath, e);
            throw new BusinessException("File writing error");
        }
    }
}
