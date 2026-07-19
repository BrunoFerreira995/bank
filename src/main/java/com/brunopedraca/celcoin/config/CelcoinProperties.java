package com.brunopedraca.celcoin.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "celcoin")
public record CelcoinProperties(
        boolean enabled,
        @NotBlank String environment,
        String baseUrl,
        String clientId,
        String clientSecret,
        @NotBlank String tokenPath,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout,
        @NotNull Duration tokenRefreshMargin,
        boolean demoEnabled,
        @Valid RetryProperties retry,
        @Valid WebhookProperties webhook) {
    public record RetryProperties(int maxAttempts, @NotNull Duration initialBackoff) {}

    public record WebhookProperties(String secret, long maxPayloadBytes, @NotNull Duration replayWindow) {}
}
