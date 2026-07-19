package com.brunopedraca.celcoin.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CelcoinTokenRequest(
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        @JsonProperty("grant_type") String grantType) {
    public static CelcoinTokenRequest clientCredentials(String clientId, String clientSecret) {
        return new CelcoinTokenRequest(clientId, clientSecret, "client_credentials");
    }
}
