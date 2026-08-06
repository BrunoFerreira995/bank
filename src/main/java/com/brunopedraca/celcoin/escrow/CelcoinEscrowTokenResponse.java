package com.brunopedraca.celcoin.escrow;

import java.time.Instant;

public record CelcoinEscrowTokenResponse(
        String accessToken, String tokenType, Long expiresIn, Instant obtainedAt) {
    public boolean usable() {
        return accessToken != null && !accessToken.isBlank() && obtainedAt != null
                && expiresIn != null && Instant.now().isBefore(obtainedAt.plusSeconds(Math.max(1, expiresIn - 60)));
    }
}
