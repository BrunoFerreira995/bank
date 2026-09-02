package com.brunopedraca.celcoin.bff.v1.identity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class TotpVerifier {
    private TotpVerifier() {}
    static boolean matches(String base32Secret, String code) {
        return matches(base32Secret, code, System.currentTimeMillis());
    }
    static boolean matches(String base32Secret, String code, long epochMillis) {
        if (base32Secret == null || !code.matches("\\d{6}")) return false;
        long step = epochMillis / 30_000;
        for (long offset = -1; offset <= 1; offset++) {
            if (MessageDigest.isEqual(code.getBytes(StandardCharsets.US_ASCII), generate(base32Secret, step + offset).getBytes(StandardCharsets.US_ASCII))) return true;
        }
        return false;
    }
    private static String generate(String secret, long step) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(base32(secret), "HmacSHA1"));
            byte[] digest = mac.doFinal(ByteBuffer.allocate(8).putLong(step).array());
            int offset = digest[digest.length - 1] & 15;
            int value = ((digest[offset] & 127) << 24) | ((digest[offset + 1] & 255) << 16) | ((digest[offset + 2] & 255) << 8) | (digest[offset + 3] & 255);
            return String.format("%06d", value % 1_000_000);
        } catch (Exception exception) { return "000000"; }
    }
    private static byte[] base32(String source) {
        String text = source.replace("=", "").replaceAll("\\s", "").toUpperCase(java.util.Locale.ROOT);
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        int buffer = 0, bits = 0;
        for (char character : text.toCharArray()) {
            int value = character >= 'A' && character <= 'Z' ? character - 'A' : character >= '2' && character <= '7' ? character - '2' + 26 : -1;
            if (value < 0) throw new IllegalArgumentException("Invalid Base32 secret");
            buffer = (buffer << 5) | value; bits += 5;
            if (bits >= 8) { output.write((buffer >> (bits - 8)) & 255); bits -= 8; }
        }
        return output.toByteArray();
    }
}
