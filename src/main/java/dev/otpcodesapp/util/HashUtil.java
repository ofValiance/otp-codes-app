package dev.otpcodesapp.util;

import org.mindrot.jbcrypt.BCrypt;


public class HashUtil {

    public static String hashGenerate(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt(12));
    }

    public static boolean checkHash(String input, String hash) {
        return BCrypt.checkpw(input, hash);
    }
}
