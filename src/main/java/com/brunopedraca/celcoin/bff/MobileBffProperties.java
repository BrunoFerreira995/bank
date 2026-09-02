package com.brunopedraca.celcoin.bff;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Settings owned by the mobile backend-for-frontend, independent from the Celcoin SDK. */
@ConfigurationProperties(prefix = "mobile.bff")
public record MobileBffProperties(boolean enabled, Duration sessionTtl, Duration refreshTtl, Duration mfaChallengeTtl, String mfaEncryptionKey) {
    public MobileBffProperties {
        sessionTtl = sessionTtl == null ? Duration.ofMinutes(15) : sessionTtl;
        refreshTtl = refreshTtl == null ? Duration.ofDays(30) : refreshTtl;
        mfaChallengeTtl = mfaChallengeTtl == null ? Duration.ofMinutes(5) : mfaChallengeTtl;
    }
}
