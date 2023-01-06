package dev.wee;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.exceptions.CodeGenerationException;

import java.time.Instant;


public class TwoFactorAuth {
    public static String generateCurrentCode(String secret)  {
        try {
            return new DefaultCodeGenerator()
                    .generate(secret, Instant.now().getEpochSecond() / 30);
        } catch (CodeGenerationException e) {
            e.printStackTrace();
            return null;
        }
    }

}


