package com.brunopedraca.celcoin.common.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class CelcoinWebClientFactoryTest {
    @Test
    void appliesSslContextProviderWhenConfigured() {
        CelcoinSslContextProvider provider = mock(CelcoinSslContextProvider.class);
        when(provider.createSslContext()).thenReturn(clientContext());

        WebClient webClient =
                CelcoinWebClientFactory.create(TestProperties.celcoin("http://localhost"), false, null, provider);

        assertThat(webClient).isNotNull();
        verify(provider).createSslContext();
    }

    private static SslContext clientContext() {
        try {
            return SslContextBuilder.forClient().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void buildsWithoutSslContextProvider() {
        WebClient webClient = CelcoinWebClientFactory.create(
                TestProperties.celcoin("http://localhost"), false, mock(CelcoinTokenService.class));

        assertThat(webClient).isNotNull();
    }

    @Test
    void apiModelsCarryValues() {
        CelcoinApiResponse<String> response = new CelcoinApiResponse<>("data", "corr", "req-1");
        CelcoinApiError error = new CelcoinApiError("CODE", "message", "details");

        assertThat(response.data()).isEqualTo("data");
        assertThat(response.correlationId()).isEqualTo("corr");
        assertThat(response.remoteRequestId()).isEqualTo("req-1");
        assertThat(error.code()).isEqualTo("CODE");
        assertThat(error.message()).isEqualTo("message");
        assertThat(error.details()).isEqualTo("details");
    }
}
