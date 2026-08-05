package com.brunopedraca.celcoin.auth;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.common.exception.CelcoinAuthenticationException;
import com.brunopedraca.celcoin.common.http.CelcoinWebClientFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CelcoinTokenClientErrorTest {
    private WireMockServer wireMock;

    @BeforeEach
    void start() {
        wireMock = new WireMockServer(0);
        wireMock.start();
    }

    @AfterEach
    void stop() {
        wireMock.stop();
    }

    @Test
    void throwsAuthenticationExceptionOnErrorStatus() {
        wireMock.stubFor(post(urlEqualTo("/v5/token"))
                .willReturn(aResponse().withStatus(401).withBody("unauthorized")));

        CelcoinTokenClient client = new CelcoinTokenClient(
                CelcoinWebClientFactory.create(TestProperties.celcoin(wireMock.baseUrl()), false, null),
                TestProperties.celcoin(wireMock.baseUrl()));

        assertThatThrownBy(client::generateToken)
                .isInstanceOf(CelcoinAuthenticationException.class)
                .satisfies(ex -> assertThat(((CelcoinAuthenticationException) ex).status())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void throwsAuthenticationExceptionWhenAccessTokenIsMissing() {
        wireMock.stubFor(post(urlEqualTo("/v5/token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));

        CelcoinTokenClient client = new CelcoinTokenClient(
                CelcoinWebClientFactory.create(TestProperties.celcoin(wireMock.baseUrl()), false, null),
                TestProperties.celcoin(wireMock.baseUrl()));

        assertThatThrownBy(client::generateToken).isInstanceOf(CelcoinAuthenticationException.class);
    }

    @Test
    void wrapsUnexpectedDecodingErrors() {
        wireMock.stubFor(post(urlEqualTo("/v5/token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{invalid json")));

        CelcoinTokenClient client = new CelcoinTokenClient(
                CelcoinWebClientFactory.create(TestProperties.celcoin(wireMock.baseUrl()), false, null),
                TestProperties.celcoin(wireMock.baseUrl()));

        assertThatThrownBy(client::generateToken).isInstanceOf(CelcoinAuthenticationException.class);
    }
}
