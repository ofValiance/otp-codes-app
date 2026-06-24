package dev.otpcodesapp.service;

public class MailingService {

    public enum Channel{
        SMS,
        EMAIL,
        TELEGRAM,
        FILE
    }

    public void mailCode(Channel channel, String destination) {

    }

    private void sendSms() {

    }

    private void sendEmail() {

    }

    private void sendTelegram() {

    }

    private void saveFile() {

    }
}
