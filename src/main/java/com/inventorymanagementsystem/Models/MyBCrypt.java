package com.inventorymanagementsystem.Models;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;


public interface MyBCrypt {
    static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return BCrypt.gensalt(12, random);
    }

    static String hashPassword(String password) {
        String salt = generateSalt();
        return BCrypt.hashpw(password, salt);
    }

    static boolean isPasswordEqual(String plaintext, String hashPassword) {
        return BCrypt.checkpw(plaintext, hashPassword);
    }
}