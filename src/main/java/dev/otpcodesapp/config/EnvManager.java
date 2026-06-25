package dev.otpcodesapp.config;


public class EnvManager {

    public static String getString(String key) {
        return System.getenv(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(System.getenv(key));
    }
}
