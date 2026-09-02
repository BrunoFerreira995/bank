package com.brunopedraca.celcoin.webhook;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.common.exception.CelcoinUnauthorizedException;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class CelcoinWebhookSignatureVerifierTest {
    private final CelcoinWebhookSignatureVerifier verifier = new CelcoinWebhookSignatureVerifier(properties("secret", false));
    @Test void rejectsMissingSignature() { assertThatThrownBy(() -> verifier.verify("{}".getBytes(StandardCharsets.UTF_8), new HttpHeaders())).isInstanceOf(CelcoinUnauthorizedException.class); }
    @Test void rejectsInvalidSignatureAndTimestamp() {
        HttpHeaders invalid = new HttpHeaders(); invalid.set("X-Celcoin-Timestamp", "not-a-time"); invalid.set("X-Celcoin-Signature", "bad");
        assertThatThrownBy(() -> verifier.verify("{}".getBytes(StandardCharsets.UTF_8), invalid)).isInstanceOf(CelcoinUnauthorizedException.class);
        HttpHeaders signature = new HttpHeaders(); signature.set("X-Celcoin-Timestamp", Long.toString(java.time.Instant.now().getEpochSecond())); signature.set("X-Celcoin-Signature", "bad");
        assertThatThrownBy(() -> verifier.verify("{}".getBytes(StandardCharsets.UTF_8), signature)).isInstanceOf(CelcoinUnauthorizedException.class);
    }
    @Test void rejectsExpiredTimestampAndMissingSecretOutsideTestMode() {
        HttpHeaders expired = new HttpHeaders(); expired.set("X-Celcoin-Timestamp", "1"); expired.set("X-Celcoin-Signature", "bad");
        assertThatThrownBy(() -> verifier.verify("{}".getBytes(StandardCharsets.UTF_8), expired)).isInstanceOf(CelcoinUnauthorizedException.class);
        assertThatThrownBy(() -> new CelcoinWebhookSignatureVerifier(properties("", false)).verify(new byte[0], new HttpHeaders())).isInstanceOf(CelcoinUnauthorizedException.class);
    }
    private static CelcoinProperties properties(String secret, boolean allowUnsigned) { return new CelcoinProperties(true,"sandbox","http://localhost","id","secret","/v5/token",Duration.ofSeconds(1),Duration.ofSeconds(1),Duration.ZERO,false,new CelcoinProperties.RetryProperties(1,Duration.ZERO),new CelcoinProperties.WebhookProperties(secret,1024,Duration.ofMinutes(5),allowUnsigned),new CelcoinProperties.SslProperties(false,null,null,null,null,null,null,null)); }
}
