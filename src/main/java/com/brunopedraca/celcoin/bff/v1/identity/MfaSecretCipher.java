package com.brunopedraca.celcoin.bff.v1.identity;

import com.brunopedraca.celcoin.bff.MobileBffProperties;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

/** Decrypts versioned AES-GCM MFA secrets provisioned by the trusted back-office. */
@Component
class MfaSecretCipher {
    private final MobileBffProperties properties;
    MfaSecretCipher(MobileBffProperties properties) { this.properties = properties; }
    String decrypt(String encrypted) {
        try {
            if (!encrypted.startsWith("v1:")) throw new IllegalArgumentException("Unsupported MFA secret format");
            byte[] payload = Base64.getDecoder().decode(encrypted.substring(3));
            byte[] key = Base64.getDecoder().decode(properties.mfaEncryptionKey());
            if (key.length != 32 || payload.length < 29) throw new IllegalArgumentException("Invalid MFA encryption material");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, Arrays.copyOf(payload, 12)));
            return new String(cipher.doFinal(payload, 12, payload.length - 12), StandardCharsets.US_ASCII);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new MobileUnauthorizedException();
        }
    }
}
