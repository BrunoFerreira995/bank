package com.brunopedraca.celcoin;

import com.brunopedraca.celcoin.config.CelcoinProperties;
import java.time.Duration;

public final class TestProperties {
    private TestProperties() {}

    public static CelcoinProperties celcoin(String baseUrl) {
        return new CelcoinProperties(
                true,
                "sandbox",
                baseUrl,
                "client-id",
                "client-secret",
                "/v5/token",
                Duration.ofSeconds(2),
                Duration.ofSeconds(5),
                Duration.ofSeconds(60),
                false,
                new CelcoinProperties.RetryProperties(3, Duration.ofMillis(10)),
                new CelcoinProperties.WebhookProperties("", 1024 * 1024, Duration.ofMinutes(5)),
                new CelcoinProperties.SslProperties(false, null, "PKCS12", null, null, null, "PKCS12", null));
    }

    public static CelcoinProperties celcoinWithSsl(String baseUrl, CelcoinProperties.SslProperties ssl) {
        return new CelcoinProperties(
                true,
                "sandbox",
                baseUrl,
                "client-id",
                "client-secret",
                "/v5/token",
                Duration.ofSeconds(2),
                Duration.ofSeconds(5),
                Duration.ofSeconds(60),
                false,
                new CelcoinProperties.RetryProperties(3, Duration.ofMillis(10)),
                new CelcoinProperties.WebhookProperties("", 1024 * 1024, Duration.ofMinutes(5)),
                ssl);
    }
}
