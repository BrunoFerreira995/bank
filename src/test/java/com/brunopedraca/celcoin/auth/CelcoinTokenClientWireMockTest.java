package com.brunopedraca.celcoin.auth;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.common.http.CelcoinWebClientFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinTokenClientWireMockTest {
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
    void postsClientCredentialsWithoutBearerToken() {
        wireMock.stubFor(post(urlEqualTo("/v5/token"))
                .withRequestBody(containing("grant_type=client_credentials"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"abc\",\"token_type\":\"Bearer\",\"expires_in\":3600}")));

        var properties = TestProperties.celcoin(wireMock.baseUrl());
        CelcoinTokenClient client =
                new CelcoinTokenClient(CelcoinWebClientFactory.create(properties, false, null), properties);

        CelcoinTokenResponse token = client.generateToken();

        assertThat(token.accessToken()).isEqualTo("abc");
        wireMock.verify(postRequestedFor(urlEqualTo("/v5/token"))
                .withoutHeader("Authorization")
                .withRequestBody(containing("client_id=client-id"))
                .withRequestBody(containing("client_secret=client-secret")));
    }
}
