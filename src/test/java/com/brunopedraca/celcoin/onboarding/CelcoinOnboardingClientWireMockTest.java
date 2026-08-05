package com.brunopedraca.celcoin.onboarding;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinWebClientFactory;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycAddress;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessOwner;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycDocument;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycFinancialDetails;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycOnboardingResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountUpdateRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycProposal;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycProposalSearchResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycUpdateResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookAuth;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class CelcoinOnboardingClientWireMockTest {
    private WireMockServer wireMock;
    private CelcoinOnboardingClient client;

    @BeforeEach
    void start() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        CelcoinTokenService tokenService = mock(CelcoinTokenService.class);
        when(tokenService.getAccessToken()).thenReturn("test-token");
        WebClient webClient =
                CelcoinWebClientFactory.create(TestProperties.celcoin(wireMock.baseUrl()), true, tokenService);
        client = new CelcoinOnboardingClient(
                new CelcoinHttpClient(webClient, TestProperties.celcoin(wireMock.baseUrl())));
    }

    @AfterEach
    void stop() {
        wireMock.stop();
    }

    @Test
    void createPersonAccountPostsToNaturalPersonEndpoint() {
        wireMock.stubFor(
                post(urlEqualTo("/onboarding/v1/onboarding-proposal/natural-person"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {"version":"1.0.0","status":"PROCESSING","body":{
                                "proposalId":"de20636c-5361-4df4-8a34-c01995a6976d",
                                "clientCode":"b9a77b3d-b519-4193-ac59-4f88de04d8a4",
                                "documentNumber":"83262483559"}}""")));

        CelcoinKycOnboardingResponse response = client.createPersonAccount(personRequest(), "kyc-pf-1");

        assertThat(response.proposalId()).isEqualTo("de20636c-5361-4df4-8a34-c01995a6976d");
        assertThat(response.documentNumber()).isEqualTo("83262483559");
    }

    @Test
    void createBusinessAccountPostsToLegalPersonEndpoint() {
        wireMock.stubFor(
                post(urlEqualTo("/onboarding/v1/onboarding-proposal/legal-person"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {"version":"1.0.0","status":"PROCESSING","body":{
                                "proposalId":"12334dfb-4c4e-43fb-ad93-fa28e3123473",
                                "clientCode":"1234ab7c-2855-436b-9d19-8abcdc198e984",
                                "documentNumber":"87649940000194"}}""")));

        CelcoinKycOnboardingResponse response = client.createBusinessAccount(businessRequest(), "kyc-pj-1");

        assertThat(response.proposalId()).isEqualTo("12334dfb-4c4e-43fb-ad93-fa28e3123473");
        assertThat(response.documentNumber()).isEqualTo("87649940000194");
    }

    @Test
    void getProposalQueriesByProposalId() {
        wireMock.stubFor(
                get(urlEqualTo("/onboarding/v1/onboarding-proposal?ProposalId="
                                + "3257b215-bef9-402b-a779-104441b520b4"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {"version":"1.0.0","status":"SUCCESS","body":{
                                "limit":200,"currentPage":1,"limitPerPage":200,"totalPages":1,"totalItems":1,
                                "proposal":[{
                                "proposalId":"3257b215-bef9-402b-a779-104441b520b4",
                                "clientCode":"7817b215-beg3-801a-a779-117771t550h2",
                                "documentNumber":"12345678909","status":"PENDING","proposalType":"PF",
                                "documentscopys":[]}]}}""")));

        CelcoinKycProposalSearchResponse response = client.getProposal("3257b215-bef9-402b-a779-104441b520b4");

        assertThat(response.totalItems()).isEqualTo(1);
        CelcoinKycProposal proposal = response.proposals().get(0);
        assertThat(proposal.proposalId()).isEqualTo("3257b215-bef9-402b-a779-104441b520b4");
        assertThat(proposal.status()).isEqualTo("PENDING");
        assertThat(proposal.proposalType()).isEqualTo("PF");
    }

    @Test
    void updatePersonAccountPutsToAccountNaturalPersonEndpoint() {
        wireMock.stubFor(
                put(urlEqualTo("/onboarding/v1/onboarding-proposal/account/30053913714179/natural-person"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {"version":"1.0.0","status":"SUCCESS","body":{
                                "updateProposalId":"12334dfb-4c4e-43fb-ad93-fa28e3123473",
                                "clientCode":"123456","documentNumber":"33333333333",
                                "account":"30053913714179"}}""")));

        CelcoinKycUpdateResponse response = client.updatePersonAccount(
                "30053913714179",
                new CelcoinKycPersonAccountUpdateRequest(
                        "123456",
                        "+5512981175554",
                        "email@email.com",
                        "Celia Silva",
                        "Carlos Silva",
                        "Carlos",
                        address()));

        assertThat(response.updateProposalId()).isEqualTo("12334dfb-4c4e-43fb-ad93-fa28e3123473");
        assertThat(response.account()).isEqualTo("30053913714179");
    }

    @Test
    void createWebhookSubscriptionPostsToWebhookSubscriptionEndpoint() {
        wireMock.stubFor(
                post(urlEqualTo("/baas/v2/webhook/subscription"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                {"version":"1.0.0","status":"SUCCESS","body":{
                                "subscriptionId":"684b45a5391dbba43a367f80"}}""")));

        CelcoinKycWebhookSubscriptionResponse response =
                client.createWebhookSubscription(new CelcoinKycWebhookSubscriptionRequest(
                        "onboarding-proposal",
                        "https://example.com/webhook",
                        new CelcoinKycWebhookAuth("login", "password", "basic")));

        assertThat(response.subscriptionId()).isEqualTo("684b45a5391dbba43a367f80");
    }

    private CelcoinKycPersonAccountRequest personRequest() {
        return new CelcoinKycPersonAccountRequest(
                "a7e9ea3f-69e4-4599-92b4-6cb8a79c3512",
                "91170215025",
                "+5511912345678",
                "testekyc@celcoin.com.br",
                "Teste Mae",
                "Teste teste",
                "",
                "31-12-2000",
                address(),
                false,
                "BAAS",
                List.of(new CelcoinKycDocument("SELFIE", "https://cloud.storage/selfie.jpeg")),
                new CelcoinKycFinancialDetails("1DINP02", null, "ONP07", "NWNP02"));
    }

    private CelcoinKycBusinessAccountRequest businessRequest() {
        return new CelcoinKycBusinessAccountRequest(
                "a7e9ea3f-69e4-4599-92b4-6cb8a79c3512",
                "+5511912345678",
                "87649940000194",
                "testekyc@celcoin.com.br",
                "Celcoin",
                "Celcoin Instituicao de Pagamento",
                "PJ",
                List.of(new CelcoinKycBusinessOwner(
                        "SOCIO",
                        "72352781027",
                        "Nome Teste",
                        "+5511912345128",
                        "sociokyc@celcoin.com.br",
                        "Nome Mae",
                        "Nome",
                        "02-02-1990",
                        address(),
                        false,
                        null)),
                address(),
                "BAAS",
                List.of(),
                null);
    }

    private CelcoinKycAddress address() {
        return new CelcoinKycAddress("06455030", "Alameda Xingu", "350", "", "Alphaville Industrial", "Barueri", "SP");
    }
}
