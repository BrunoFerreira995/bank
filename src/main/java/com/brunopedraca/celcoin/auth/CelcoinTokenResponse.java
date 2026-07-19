package com.brunopedraca.celcoin.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record CelcoinTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn,
        @JsonAlias("scope") String scope,
        Instant obtainedAt) {
    public CelcoinTokenResponse withObtainedAt(Instant instant) {
        return new CelcoinTokenResponse(accessToken, tokenType, expiresIn, scope, instant);
    }

    public Instant expiresAt() {
        return obtainedAt.plusSeconds(expiresIn);
    }
}
