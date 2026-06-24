package dev.otpcodesapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mindrot.jbcrypt.BCrypt;


public class HashUtil {

    private static final Logger logger = LoggerFactory.getLogger(HashUtil.class);

    public static String hashGenerate(String input) {
        logger.debug("Generating BCrypt hash for input");
        String hash = BCrypt.hashpw(input, BCrypt.gensalt(12));
        logger.debug("BCrypt hash successfully generated");
        return hash;
    }

    public static boolean checkHash(String input, String hash) {
        logger.debug("Checking BCrypt hash");
        boolean matches = BCrypt.checkpw(input, hash);
        logger.debug("BCrypt hash check result: {}", matches);
        return matches;
    }
}
