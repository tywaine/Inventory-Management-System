package com.inventorymanagementsystem.Models;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

public class EncryptionUtils {
    private static final String ALGORITHM = "AES";
    private static String KEY;

    static {
        try (InputStream input = EncryptionUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties properties = new Properties();

            if (input != null) {
                properties.load(input);
                KEY = properties.getProperty("secret_key");
            }
            else {
                throw new RuntimeException("config.properties not found in resources folder.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String password){
        try{
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String encryptedPassword){
        try{
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
