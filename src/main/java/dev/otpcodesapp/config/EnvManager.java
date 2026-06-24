package dev.otpcodesapp.config;

import io.github.cdimascio.dotenv.Dotenv;


public class EnvManager {

    public static String get(String key) {
        Dotenv d = Dotenv.load();
        return d.get(key);
    }
}
