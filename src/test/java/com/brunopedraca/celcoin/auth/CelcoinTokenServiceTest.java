package com.brunopedraca.celcoin.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.TestProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class CelcoinTokenServiceTest {
    private final Instant now = Instant.parse("2026-07-19T12:00:00Z");

    @Test
    void reusesCachedTokenBeforeRefreshMargin() {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        when(tokenClient.generateToken()).thenReturn(new CelcoinTokenResponse("token-1", "Bearer", 3600, null, now));

        CelcoinTokenService service = new CelcoinTokenService(
                tokenClient, TestProperties.celcoin("http://localhost"), Clock.fixed(now, ZoneOffset.UTC));

        assertThat(service.getAccessToken()).isEqualTo("token-1");
        assertThat(service.getAccessToken()).isEqualTo("token-1");
        verify(tokenClient, times(1)).generateToken();
    }

    @Test
    void refreshesTokenInsideRefreshMargin() {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        when(tokenClient.generateToken())
                .thenReturn(new CelcoinTokenResponse("token-1", "Bearer", 30, null, now))
                .thenReturn(new CelcoinTokenResponse("token-2", "Bearer", 3600, null, now));

        CelcoinTokenService service = new CelcoinTokenService(
                tokenClient, TestProperties.celcoin("http://localhost"), Clock.fixed(now, ZoneOffset.UTC));

        assertThat(service.getAccessToken()).isEqualTo("token-1");
        assertThat(service.getAccessToken()).isEqualTo("token-2");
        verify(tokenClient, times(2)).generateToken();
    }

    @Test
    void evictsCachedTokenAndFetchesAgain() {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        when(tokenClient.generateToken())
                .thenReturn(new CelcoinTokenResponse("token-1", "Bearer", 3600, null, now))
                .thenReturn(new CelcoinTokenResponse("token-2", "Bearer", 3600, null, now));

        CelcoinTokenService service = new CelcoinTokenService(
                tokenClient, TestProperties.celcoin("http://localhost"), Clock.fixed(now, ZoneOffset.UTC));

        service.getAccessToken();
        service.evictToken();
        service.getAccessToken();

        verify(tokenClient, times(2)).generateToken();
    }

    @Test
    void refreshesTokenWithoutObtainedAt() {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        when(tokenClient.generateToken())
                .thenReturn(new CelcoinTokenResponse("token-1", "Bearer", 3600, null, null))
                .thenReturn(new CelcoinTokenResponse("token-2", "Bearer", 3600, null, now));

        CelcoinTokenService service = new CelcoinTokenService(
                tokenClient, TestProperties.celcoin("http://localhost"), Clock.fixed(now, ZoneOffset.UTC));

        assertThat(service.getAccessToken()).isEqualTo("token-1");
        assertThat(service.getAccessToken()).isEqualTo("token-2");
        verify(tokenClient, times(2)).generateToken();
    }

    @Test
    void usesSystemClockViaPublicConstructor() {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        when(tokenClient.generateToken())
                .thenReturn(new CelcoinTokenResponse("token-1", "Bearer", 3600, null, Instant.now()));

        CelcoinTokenService service = new CelcoinTokenService(tokenClient, TestProperties.celcoin("http://localhost"));

        assertThat(service.getAccessToken()).isEqualTo("token-1");
    }

    @Test
    void returnsCachedTokenWhenAnotherThreadRefreshesDuringWait() throws Exception {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        CountDownLatch insideRefresh = new CountDownLatch(1);
        CountDownLatch releaseRefresh = new CountDownLatch(1);
        when(tokenClient.generateToken()).thenAnswer(invocation -> {
            insideRefresh.countDown();
            releaseRefresh.await(5, TimeUnit.SECONDS);
            return new CelcoinTokenResponse("token-1", "Bearer", 3600, null, now);
        });
        CelcoinTokenService service = new CelcoinTokenService(
                tokenClient, TestProperties.celcoin("http://localhost"), Clock.fixed(now, ZoneOffset.UTC));

        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> first = pool.submit(service::getAccessToken);
        insideRefresh.await();
        Future<String> second = pool.submit(service::getAccessToken);
        Thread.sleep(200);
        releaseRefresh.countDown();

        assertThat(first.get()).isEqualTo("token-1");
        assertThat(second.get()).isEqualTo("token-1");
        verify(tokenClient, times(1)).generateToken();
        pool.shutdownNow();
    }

    @Test
    void buildsClientCredentialsRequest() {
        CelcoinTokenRequest request = CelcoinTokenRequest.clientCredentials("cid", "csecret");

        assertThat(request.clientId()).isEqualTo("cid");
        assertThat(request.clientSecret()).isEqualTo("csecret");
        assertThat(request.grantType()).isEqualTo("client_credentials");
    }
}
