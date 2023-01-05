package dev.wee;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class TwoFactorAuth {
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final int NUM_DIGITS = 6;
    private static final int WINDOW = 30;

    public static String generateCode(String secret) {
        // Get current timestamp
        long timestamp = Instant.now().getEpochSecond() / WINDOW;

        // Convert secret to byte array
        byte[] secretBytes = secret.getBytes();

        // Generate HMAC-SHA1 of timestamp using secret
        try {
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            hmac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
            byte[] hmacBytes = hmac.doFinal(String.valueOf(timestamp).getBytes());

            // Truncate HMAC-SHA1
            int offset = hmacBytes[hmacBytes.length - 1] & 0x0f;
            int truncatedHash = 0;
            for (int i = 0; i < NUM_DIGITS; i++) {
                truncatedHash <<= 8;
                truncatedHash |= (hmacBytes[offset + i] & 0xff);
            }

            // Generate code by taking the last NUM_DIGITS digits of the truncated hash
            int code = truncatedHash % (int) Math.pow(10, NUM_DIGITS);
            return String.format("%0" + NUM_DIGITS + "d", code);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

}
