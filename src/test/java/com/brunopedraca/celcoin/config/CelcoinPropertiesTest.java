package com.brunopedraca.celcoin.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class CelcoinPropertiesTest {
    @Test
    void sslPropertiesDefaultsStoreTypeAndHaveValueSemantics() {
        CelcoinProperties.SslProperties ssl =
                new CelcoinProperties.SslProperties(true, "/ks.p12", null, "pass", null, null, null, null);
        CelcoinProperties.SslProperties same =
                new CelcoinProperties.SslProperties(true, "/ks.p12", null, "pass", null, null, null, null);
        CelcoinProperties.SslProperties other =
                new CelcoinProperties.SslProperties(true, "/ks2.p12", null, "pass", null, null, null, null);

        assertThat(ssl.keystoreType()).isEqualTo("PKCS12");
        assertThat(ssl.truststoreType()).isEqualTo("PKCS12");
        assertThat(ssl).isEqualTo(same).hasSameHashCodeAs(same);
        assertThat(ssl).isNotEqualTo(other);
        assertThat(ssl.toString()).contains("/ks.p12");
    }

    @Test
    void fullPropertiesRecordExposesAllFields() {
        CelcoinProperties properties = new CelcoinProperties(
                true,
                "sandbox",
                "https://celcoin.example",
                "cid",
                "csecret",
                "/v5/token",
                Duration.ofSeconds(2),
                Duration.ofSeconds(20),
                Duration.ofSeconds(60),
                true,
                new CelcoinProperties.RetryProperties(3, Duration.ofMillis(500)),
                new CelcoinProperties.WebhookProperties("secret", 1024, Duration.ofMinutes(5)),
                new CelcoinProperties.SslProperties(false, null, "PKCS12", null, null, null, "PKCS12", null));

        assertThat(properties.environment()).isEqualTo("sandbox");
        assertThat(properties.baseUrl()).isEqualTo("https://celcoin.example");
        assertThat(properties.clientId()).isEqualTo("cid");
        assertThat(properties.tokenPath()).isEqualTo("/v5/token");
        assertThat(properties.demoEnabled()).isTrue();
        assertThat(properties.retry().maxAttempts()).isEqualTo(3);
        assertThat(properties.webhook().secret()).isEqualTo("secret");
        assertThat(properties.ssl().enabled()).isFalse();
        assertThat(properties.toString()).contains("https://celcoin.example");
    }
}
