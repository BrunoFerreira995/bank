package com.brunopedraca.celcoin.credit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "celcoin.credit")
public record CelcoinCreditProperties(
        boolean enabled,
        String apiBaseUrl,
        String authUrl,
        String clientId,
        String clientSecret) {
    public CelcoinCreditProperties {
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            apiBaseUrl = "https://sandbox.platform.flowfinance.com.br";
        }
        if (authUrl == null || authUrl.isBlank()) {
            authUrl = "https://sandbox.auth.flowfinance.com.br/oauth2/token";
        }
    }
}
