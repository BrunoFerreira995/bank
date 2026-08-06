package com.brunopedraca.celcoin.cards;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardAccountRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardAccountResponse;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardListRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardListResponse;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinWebClientFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CelcoinCardClientWireMockTest {
    private WireMockServer wireMock;
    private CelcoinCardClient client;

    @BeforeEach
    void start() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        CelcoinTokenService tokenService = Mockito.mock(CelcoinTokenService.class);
        Mockito.when(tokenService.getAccessToken()).thenReturn("test-token");
        var webClient = CelcoinWebClientFactory.create(
                TestProperties.celcoin(wireMock.baseUrl()), true, tokenService);
        client = new CelcoinCardClient(new CelcoinHttpClient(
                webClient, TestProperties.celcoin(wireMock.baseUrl())));
    }

    @AfterEach
    void stop() {
        wireMock.stop();
    }

    @Test
    void createsCardAccountWithIdempotency() {
        wireMock.stubFor(post(urlEqualTo("/cards/v1/accounts"))
                .withHeader("Idempotency-Key", equalTo("account-1"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBody("{\"cardAccountId\":\"account-1\",\"document\":\"123\",\"name\":\"Maria\",\"status\":\"ACTIVE\"}")));

        CelcoinCardAccountResponse response = client.createCardAccount(
                new CelcoinCardAccountRequest("123", "Maria", "maria@example.com", null, null, null), "account-1");

        assertThat(response.cardAccountId()).isEqualTo("account-1");
    }

    @Test
    void listsCardsUsingOfficialAccountAndPaginationFilters() {
        wireMock.stubFor(get(urlPathEqualTo("/cards/v1/cards"))
                .withQueryParam("accountId", equalTo("account-1"))
                .withQueryParam("status", equalTo("ACTIVE"))
                .withQueryParam("type", equalTo("PHYSICAL"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("size", equalTo("20"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBody("{\"cards\":[],\"page\":0,\"size\":20,\"total\":0}")));

        CelcoinCardListResponse response = client.listCards(
                new CelcoinCardListRequest("account-1", "ACTIVE", "PHYSICAL", 0, 20));

        assertThat(response.cards()).isEmpty();
        wireMock.verify(getRequestedFor(urlPathEqualTo("/cards/v1/cards")));
    }
}
