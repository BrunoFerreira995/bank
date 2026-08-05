package com.brunopedraca.celcoin.common.http;

import java.time.Duration;
import org.springframework.http.HttpHeaders;

/**
 * Rate-limit metadata read from Celcoin response headers. Supports both the
 * generic {@code Retry-After} header and common {@code X-RateLimit-*} naming
 * conventions used by API gateways.
 */
public record RateLimitInfo(Long limit, Long remaining, Duration resetAfter, Duration retryAfter) {
    public static RateLimitInfo from(HttpHeaders headers) {
        Long limit = firstLong(headers, "X-RateLimit-Limit", "RateLimit-Limit", "X-Rate-Limit-Limit");
        Long remaining = firstLong(headers, "X-RateLimit-Remaining", "RateLimit-Remaining", "X-Rate-Limit-Remaining");
        Duration resetAfter = firstSeconds(headers, "X-RateLimit-Reset-After", "RateLimit-Reset-After");
        Duration retryAfter = firstSeconds(headers, "Retry-After");
        return new RateLimitInfo(limit, remaining, resetAfter, retryAfter);
    }

    private static Long firstLong(HttpHeaders headers, String... names) {
        for (String name : names) {
            String value = headers.getFirst(name);
            if (value != null) {
                try {
                    return Long.parseLong(value.trim());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private static Duration firstSeconds(HttpHeaders headers, String... names) {
        for (String name : names) {
            String value = headers.getFirst(name);
            if (value != null) {
                try {
                    return Duration.ofSeconds(Long.parseLong(value.trim()));
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }
}
