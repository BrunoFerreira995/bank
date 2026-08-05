package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.common.exception.CelcoinUnauthorizedException;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class CelcoinWebhookSignatureVerifier {
    private final CelcoinProperties properties;

    public CelcoinWebhookSignatureVerifier(CelcoinProperties properties) {
        this.properties = properties;
    }

    public void verify(byte[] payload, HttpHeaders headers) {
        String secret = properties.webhook().secret();
        if (secret == null || secret.isBlank()) {
            return;
        }
        String timestamp = headers.getFirst("X-Celcoin-Timestamp");
        String signature = headers.getFirst("X-Celcoin-Signature");
        if (timestamp == null || signature == null) {
            throw new CelcoinUnauthorizedException("Missing Celcoin webhook signature headers");
        }
        long epochSeconds = Long.parseLong(timestamp);
        long skew = Math.abs(Instant.now().getEpochSecond() - epochSeconds);
        if (skew > properties.webhook().replayWindow().toSeconds()) {
            throw new CelcoinUnauthorizedException("Celcoin webhook timestamp is outside the replay window");
        }
        String expected = hmac(secret, timestamp + "." + new String(payload, StandardCharsets.UTF_8));
        if (!MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8))) {
            throw new CelcoinUnauthorizedException("Invalid Celcoin webhook signature");
        }
    }

    private String hmac(String secret, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new CelcoinUnauthorizedException("Could not validate Celcoin webhook signature");
        }
    }
}
