package com.brunopedraca.celcoin.escrow;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "celcoin.escrow")
public record CelcoinEscrowProperties(
        boolean enabled,
        String apiBaseUrl,
        String authUrl,
        String clientId,
        String clientSecret) {
    public CelcoinEscrowProperties {
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            apiBaseUrl = "https://sandbox.platform.credit.celcoin.com.br/escrow/api";
        }
        if (authUrl == null || authUrl.isBlank()) {
            authUrl = "https://sandbox.auth.flowfinance.com.br/oauth2/token";
        }
    }
}
