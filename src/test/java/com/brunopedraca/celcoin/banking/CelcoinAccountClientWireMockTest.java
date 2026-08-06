package com.brunopedraca.celcoin.banking;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementResponse;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CelcoinAccountClientWireMockTest {
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
    void getStatementSendsOfficialPaginationParameters() {
        wireMock.stubFor(get(urlPathEqualTo("/baas/v2/wallet/movement"))
                .withQueryParam("Account", equalTo("account-1"))
                .withQueryParam("Page", equalTo("1"))
                .withQueryParam("Limit", equalTo("25"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"accountId":"account-1","transactions":[{"transactionId":"tx-1","amount":10.50}],"page":1,"size":25,"total":51,"hasNext":true}
                                """)));

        CelcoinTokenService tokenService = Mockito.mock(CelcoinTokenService.class);
        Mockito.when(tokenService.getAccessToken()).thenReturn("test-token");
        CelcoinHttpClient http = new CelcoinHttpClient(
                com.brunopedraca.celcoin.common.http.CelcoinWebClientFactory.create(
                        TestProperties.celcoin(wireMock.baseUrl()), true, tokenService),
                TestProperties.celcoin(wireMock.baseUrl()));
        CelcoinStatementResponse response = new CelcoinAccountClient(http).getStatement(
                new CelcoinStatementRequest("account-1", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 1, 25));

        assertThat(response.transactions()).hasSize(1);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.total()).isEqualTo(51L);
        assertThat(response.hasNext()).isTrue();
        wireMock.verify(getRequestedFor(urlPathEqualTo("/baas/v2/wallet/movement"))
                .withQueryParam("DateFrom", equalTo("2026-01-01"))
                .withQueryParam("DateTo", equalTo("2026-01-31")));
    }
}
