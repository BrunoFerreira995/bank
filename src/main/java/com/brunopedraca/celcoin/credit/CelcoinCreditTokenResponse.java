package com.brunopedraca.celcoin.credit;

import java.time.Instant;

public record CelcoinCreditTokenResponse(
        String accessToken, String tokenType, Long expiresIn, Instant obtainedAt) {
    public boolean usable() {
        return accessToken != null && !accessToken.isBlank() && obtainedAt != null
                && expiresIn != null && Instant.now().isBefore(obtainedAt.plusSeconds(expiresIn - 60));
    }
}
