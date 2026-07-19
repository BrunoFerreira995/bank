package com.brunopedraca.celcoin.auth;

import com.brunopedraca.celcoin.config.CelcoinProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public class CelcoinTokenService {
    private static final String TOKEN_KEY = "celcoin-token";
    private final CelcoinTokenClient tokenClient;
    private final CelcoinProperties properties;
    private final Clock clock;
    private final Cache<String, CelcoinTokenResponse> cache;
    private final ReentrantLock refreshLock = new ReentrantLock();

    public CelcoinTokenService(CelcoinTokenClient tokenClient, CelcoinProperties properties) {
        this(tokenClient, properties, Clock.systemUTC());
    }

    CelcoinTokenService(CelcoinTokenClient tokenClient, CelcoinProperties properties, Clock clock) {
        this.tokenClient = tokenClient;
        this.properties = properties;
        this.clock = clock;
        this.cache = Caffeine.newBuilder().maximumSize(1).build();
    }

    public CelcoinTokenResponse getToken() {
        CelcoinTokenResponse current = cache.getIfPresent(TOKEN_KEY);
        if (isUsable(current)) {
            return current;
        }
        refreshLock.lock();
        try {
            current = cache.getIfPresent(TOKEN_KEY);
            if (isUsable(current)) {
                return current;
            }
            CelcoinTokenResponse refreshed = tokenClient.generateToken();
            cache.put(TOKEN_KEY, refreshed);
            return refreshed;
        } finally {
            refreshLock.unlock();
        }
    }

    public String getAccessToken() {
        return getToken().accessToken();
    }

    public void evictToken() {
        cache.invalidate(TOKEN_KEY);
    }

    private boolean isUsable(CelcoinTokenResponse token) {
        if (token == null || token.obtainedAt() == null) {
            return false;
        }
        Instant refreshAt = token.expiresAt().minus(properties.tokenRefreshMargin());
        return clock.instant().isBefore(refreshAt);
    }
}
