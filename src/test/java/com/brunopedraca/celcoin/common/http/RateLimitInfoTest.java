package com.brunopedraca.celcoin.common.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class RateLimitInfoTest {
    @Test
    void parsesCommonRateLimitHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Limit", "100");
        headers.add("X-RateLimit-Remaining", "0");
        headers.add("X-RateLimit-Reset-After", "30");
        headers.add("Retry-After", "12");

        RateLimitInfo info = RateLimitInfo.from(headers);

        assertThat(info.limit()).isEqualTo(100L);
        assertThat(info.remaining()).isZero();
        assertThat(info.resetAfter()).isEqualTo(Duration.ofSeconds(30));
        assertThat(info.retryAfter()).isEqualTo(Duration.ofSeconds(12));
    }

    @Test
    void toleratesMissingAndMalformedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Limit", "abc");

        RateLimitInfo info = RateLimitInfo.from(headers);

        assertThat(info.limit()).isNull();
        assertThat(info.remaining()).isNull();
        assertThat(info.resetAfter()).isNull();
        assertThat(info.retryAfter()).isNull();
    }
}
