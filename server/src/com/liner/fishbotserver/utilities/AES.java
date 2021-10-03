package com.liner.fishbotserver.utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AES {
    public final static String KEY = "dkJSAi980dkJSAi980daS2maOdaS2mas";
    private static final String CHIPER =  "AES/ECB/PKCS5Padding";
    private static final String TYPE =  "AES";

    public static String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(CHIPER);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), TYPE));
            return new String(Base64.getEncoder().encode(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8))));
        }catch(Exception e) {
            e.printStackTrace();
            return "Encrypt filed!";
        }
    }


    public static String decrypt(String string) {
        try {
            Cipher cipher = Cipher.getInstance(CHIPER);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), TYPE));
            return new String(cipher.doFinal(Base64.getDecoder().decode(string)));
        }catch(Exception e) {
            return "Failed decrypt!";
        }
    }
}
