package com.brunopedraca.celcoin.bff.v1.identity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PasswordHasher {
    private static final int ITERATIONS = 210_000;
    private static final String DUMMY_HASH = "210000:AAAAAAAAAAAAAAAAAAAAAA==:oO2RRQr0twxbDSccNyBpLrEXnPGHtIeeQz+y0TFN4Js=";
    private PasswordHasher() {}

    static boolean matches(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            byte[] actual = derive(password, Base64.getDecoder().decode(parts[1]), Integer.parseInt(parts[0]));
            return MessageDigest.isEqual(actual, Base64.getDecoder().decode(parts[2]));
        } catch (RuntimeException | java.security.GeneralSecurityException exception) {
            return false;
        }
    }

    static boolean dummyMatches(String password) { return matches(password, DUMMY_HASH); }

    static String hash(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            byte[] value = derive(password, salt, ITERATIONS);
            return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(value);
        } catch (java.security.GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations) throws java.security.GeneralSecurityException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }
}
