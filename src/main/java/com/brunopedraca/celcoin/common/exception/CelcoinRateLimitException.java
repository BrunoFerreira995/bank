package com.brunopedraca.celcoin.common.exception;

import com.brunopedraca.celcoin.common.http.RateLimitInfo;
import java.time.Duration;

public class CelcoinRateLimitException extends CelcoinException {
    private final RateLimitInfo rateLimit;

    public CelcoinRateLimitException(String message) {
        this(message, new RateLimitInfo(null, null, null, null));
    }

    public CelcoinRateLimitException(String message, RateLimitInfo rateLimit) {
        super(message);
        this.rateLimit = rateLimit;
    }

    public RateLimitInfo rateLimit() {
        return rateLimit;
    }

    public Duration retryAfter() {
        if (rateLimit != null && rateLimit.retryAfter() != null) {
            return rateLimit.retryAfter();
        }
        if (rateLimit != null && rateLimit.resetAfter() != null) {
            return rateLimit.resetAfter();
        }
        return null;
    }
}
