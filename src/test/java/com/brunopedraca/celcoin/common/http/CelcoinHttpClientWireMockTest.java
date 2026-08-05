package com.brunopedraca.celcoin.common.http;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.common.exception.CelcoinRateLimitException;
import com.brunopedraca.celcoin.common.idempotency.CelcoinIdempotencyService;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class CelcoinHttpClientWireMockTest {
    private WireMockServer wireMock;
    private CelcoinTokenService tokenService;

    @BeforeEach
    void start() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        tokenService = mock(CelcoinTokenService.class);
        when(tokenService.getAccessToken()).thenReturn("test-token");
    }

    @AfterEach
    void stop() {
        wireMock.stop();
    }

    @Test
    void postSendsIdempotencyKeyAndCompletesRecord() {
        wireMock.stubFor(post(urlEqualTo("/v2/pix/cashout"))
                .withHeader("Idempotency-Key", equalTo("key-1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"tx-1\",\"status\":\"COMPLETED\"}")));

        CelcoinIdempotencyService idempotency = mock(CelcoinIdempotencyService.class);
        when(idempotency.begin("key-1", "/v2/pix/cashout", "{\"amount\":10}")).thenReturn(Optional.empty());
        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties(), idempotency);

        PaymentResponse response = client.post(
                "/v2/pix/cashout",
                "{\"amount\":10}",
                PaymentResponse.class,
                new CelcoinRequestContext("test-correlation", "key-1"));

        assertThat(response.id()).isEqualTo("tx-1");
        wireMock.verify(postRequestedFor(urlEqualTo("/v2/pix/cashout"))
                .withHeader("Idempotency-Key", equalTo("key-1"))
                .withHeader("X-Correlation-Id", equalTo("test-correlation")));
        verify(idempotency).complete("key-1", response);
    }

    @Test
    void postReplaysCachedResponseWithoutCallingCelcoin() {
        CelcoinIdempotencyService idempotency = mock(CelcoinIdempotencyService.class);
        when(idempotency.begin("key-1", "/v2/pix/cashout", "{\"amount\":10}"))
                .thenReturn(Optional.of("{\"id\":\"tx-1\",\"status\":\"COMPLETED\"}"));
        when(idempotency.deserialize("{\"id\":\"tx-1\",\"status\":\"COMPLETED\"}", PaymentResponse.class))
                .thenReturn(new PaymentResponse("tx-1", "COMPLETED"));
        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties(), idempotency);

        PaymentResponse response = client.post(
                "/v2/pix/cashout", "{\"amount\":10}", PaymentResponse.class, CelcoinRequestContext.create("key-1"));

        assertThat(response.id()).isEqualTo("tx-1");
        wireMock.verify(0, postRequestedFor(urlEqualTo("/v2/pix/cashout")));
        verify(idempotency, never()).complete("key-1", response);
    }

    @Test
    void postFailsRecordWhenCelcoinReturnsError() {
        wireMock.stubFor(post(urlEqualTo("/v2/pix/cashout"))
                .willReturn(aResponse().withStatus(400).withBody("{\"message\":\"invalid\"}")));

        CelcoinIdempotencyService idempotency = mock(CelcoinIdempotencyService.class);
        when(idempotency.begin("key-1", "/v2/pix/cashout", "{\"amount\":10}")).thenReturn(Optional.empty());
        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties(), idempotency);

        assertThatThrownBy(() -> client.post(
                        "/v2/pix/cashout",
                        "{\"amount\":10}",
                        PaymentResponse.class,
                        CelcoinRequestContext.create("key-1")))
                .isInstanceOf(CelcoinApiException.class);

        verify(idempotency).fail("key-1", "{\"message\":\"invalid\"}");
    }

    @Test
    void throwsRateLimitExceptionWithRetryAfterOn429() {
        wireMock.stubFor(post(urlEqualTo("/v2/pix/cashout"))
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader("Retry-After", "5")
                        .withHeader("X-RateLimit-Limit", "100")
                        .withHeader("X-RateLimit-Remaining", "0")
                        .withBody("rate limited")));

        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties());

        assertThatThrownBy(() -> client.post(
                        "/v2/pix/cashout",
                        "{\"amount\":10}",
                        PaymentResponse.class,
                        CelcoinRequestContext.create("key-1")))
                .isInstanceOf(CelcoinRateLimitException.class)
                .satisfies(ex -> {
                    CelcoinRateLimitException rate = (CelcoinRateLimitException) ex;
                    assertThat(rate.rateLimit().limit()).isEqualTo(100L);
                    assertThat(rate.rateLimit().remaining()).isZero();
                    assertThat(rate.retryAfter()).hasSeconds(5);
                });
    }

    @Test
    void failsRecordWhenRateLimited() {
        wireMock.stubFor(post(urlEqualTo("/v2/pix/cashout"))
                .willReturn(aResponse().withStatus(429).withBody("rate limited")));

        CelcoinIdempotencyService idempotency = mock(CelcoinIdempotencyService.class);
        when(idempotency.begin("key-1", "/v2/pix/cashout", "{\"amount\":10}")).thenReturn(Optional.empty());
        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties(), idempotency);

        assertThatThrownBy(() -> client.post(
                        "/v2/pix/cashout",
                        "{\"amount\":10}",
                        PaymentResponse.class,
                        CelcoinRequestContext.create("key-1")))
                .isInstanceOf(CelcoinRateLimitException.class);

        verify(idempotency).fail("key-1", "Rate limit exceeded by Celcoin API: rate limited");
    }

    @Test
    void usesDefaultMessageWhenErrorBodyIsBlank() {
        wireMock.stubFor(
                post(urlEqualTo("/v2/pix/cashout")).willReturn(aResponse().withStatus(400)));

        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties(), null);

        assertThatThrownBy(() -> client.post(
                        "/v2/pix/cashout",
                        "{\"amount\":10}",
                        PaymentResponse.class,
                        CelcoinRequestContext.create("key-1")))
                .isInstanceOf(CelcoinApiException.class)
                .hasMessageContaining("Celcoin API error");
    }

    @Test
    void retriesTransientServerErrorWithMinimumBackoff() {
        wireMock.stubFor(post(urlEqualTo("/v2/pix/cashout"))
                .willReturn(aResponse().withStatus(500).withBody("boom")));

        CelcoinHttpClient client = new CelcoinHttpClient(
                authenticatedClient(),
                new com.brunopedraca.celcoin.config.CelcoinProperties(
                        true,
                        "sandbox",
                        wireMock.baseUrl(),
                        "client-id",
                        "client-secret",
                        "/v5/token",
                        java.time.Duration.ofSeconds(2),
                        java.time.Duration.ofSeconds(5),
                        java.time.Duration.ofSeconds(60),
                        false,
                        new com.brunopedraca.celcoin.config.CelcoinProperties.RetryProperties(
                                3, java.time.Duration.ZERO),
                        new com.brunopedraca.celcoin.config.CelcoinProperties.WebhookProperties(
                                "", 1024 * 1024, java.time.Duration.ofMinutes(5)),
                        new com.brunopedraca.celcoin.config.CelcoinProperties.SslProperties(
                                false, null, "PKCS12", null, null, null, "PKCS12", null)),
                null);

        assertThatThrownBy(() -> client.post(
                        "/v2/pix/cashout",
                        "{\"amount\":10}",
                        PaymentResponse.class,
                        CelcoinRequestContext.create("key-1")))
                .isInstanceOf(CelcoinApiException.class);

        wireMock.verify(postRequestedFor(urlEqualTo("/v2/pix/cashout")));
    }

    @Test
    void downloadsBinaryContent() {
        wireMock.stubFor(get(urlEqualTo("/v2/boletos/1/pdf"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(new byte[] {1, 2, 3, 4})
                        .withHeader("Content-Type", "application/pdf")));

        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties(), null);

        byte[] bytes = client.download("/v2/boletos/1/pdf", null);

        assertThat(bytes).containsExactly(1, 2, 3, 4);
    }

    @Test
    void getReturnsParsedBodyAndDoesNotUseIdempotency() {
        wireMock.stubFor(get(urlEqualTo("/v2/accounts/1/balance"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"balance\":10.5}")));

        CelcoinIdempotencyService idempotency = mock(CelcoinIdempotencyService.class);
        CelcoinHttpClient client = new CelcoinHttpClient(authenticatedClient(), properties(), idempotency);

        BalanceResponse response = client.get("/v2/accounts/1/balance", BalanceResponse.class, null);

        assertThat(response.balance()).isEqualTo(10.5);
        wireMock.verify(getRequestedFor(urlEqualTo("/v2/accounts/1/balance")));
        verify(idempotency, never())
                .begin(
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.any());
    }

    private com.brunopedraca.celcoin.config.CelcoinProperties properties() {
        return TestProperties.celcoin(wireMock.baseUrl());
    }

    private WebClient authenticatedClient() {
        return CelcoinWebClientFactory.create(properties(), true, tokenService);
    }

    record PaymentResponse(String id, String status) {}

    record BalanceResponse(double balance) {}
}
