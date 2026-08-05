package com.brunopedraca.celcoin.onboarding;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycAddress;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessOwner;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycDocument;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycFinancialDetails;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycOnboardingResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycProposal;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycProposalSearchResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycUpdateResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookAuth;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class OnboardingDtosSerializationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void personAccountRequestSerializesWithOfficialFieldNames() throws Exception {
        CelcoinKycPersonAccountRequest request = new CelcoinKycPersonAccountRequest(
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

        String json = mapper.writeValueAsString(request);

        assertThat(json).contains("\"clientCode\":\"a7e9ea3f-69e4-4599-92b4-6cb8a79c3512\"");
        assertThat(json).contains("\"documentNumber\":\"91170215025\"");
        assertThat(json).contains("\"birthDate\":\"31-12-2000\"");
        assertThat(json).contains("\"onboardingType\":\"BAAS\"");
        assertThat(json).contains("\"files\":[{\"type\":\"SELFIE\",\"data\":\"https://cloud.storage/selfie.jpeg\"}]");
        assertThat(json).contains("\"financialDetails\":{\"declaredIncome\":\"1DINP02\",");
        assertThat(json).contains("\"isPoliticallyExposedPerson\":false");
        assertThat(json).contains("\"postalCode\":\"06455030\"");
    }

    @Test
    void businessAccountRequestSerializesWithOfficialFieldNames() throws Exception {
        CelcoinKycBusinessAccountRequest request = new CelcoinKycBusinessAccountRequest(
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

        String json = mapper.writeValueAsString(request);

        assertThat(json).contains("\"contactNumber\":\"+5511912345678\"");
        assertThat(json).contains("\"businessEmail\":\"testekyc@celcoin.com.br\"");
        assertThat(json).contains("\"businessName\":\"Celcoin\"");
        assertThat(json).contains("\"tradingName\":\"Celcoin Instituicao de Pagamento\"");
        assertThat(json).contains("\"companyType\":\"PJ\"");
        assertThat(json).contains("\"businessAddress\":");
        assertThat(json).contains("\"owner\":[{\"ownerType\":\"SOCIO\"");
    }

    @Test
    void onboardingResponseDeserializesFromEnvelope() throws Exception {
        CelcoinKycOnboardingResponse response = mapper.readValue(
                """
                {"version":"1.0.0","status":"PROCESSING","body":{
                "proposalId":"de20636c-5361-4df4-8a34-c01995a6976d",
                "clientCode":"b9a77b3d-b519-4193-ac59-4f88de04d8a4",
                "documentNumber":"83262483559"}}""",
                CelcoinKycOnboardingResponse.class);

        assertThat(response.proposalId()).isEqualTo("de20636c-5361-4df4-8a34-c01995a6976d");
        assertThat(response.clientCode()).isEqualTo("b9a77b3d-b519-4193-ac59-4f88de04d8a4");
        assertThat(response.documentNumber()).isEqualTo("83262483559");
    }

    @Test
    void proposalSearchResponseDeserializesFromEnvelope() throws Exception {
        CelcoinKycProposalSearchResponse response = mapper.readValue(
                """
                {"version":"1.0.0","status":"SUCCESS","body":{
                "limit":200,"currentPage":1,"limitPerPage":200,"totalPages":1,"totalItems":1,
                "proposal":[{
                "proposalId":"3257b215-bef9-402b-a779-104441b520b4",
                "documentNumber":"12345678909","status":"APPROVED","proposalType":"PF",
                "documentscopys":[{"createdAt":"2024-03-06T13:19:40Z"}]}]}}""",
                CelcoinKycProposalSearchResponse.class);

        assertThat(response.totalItems()).isEqualTo(1);
        CelcoinKycProposal proposal = response.proposals().get(0);
        assertThat(proposal.status()).isEqualTo("APPROVED");
        assertThat(proposal.proposalType()).isEqualTo("PF");
        assertThat(proposal.documentscopys()).hasSize(1);
    }

    @Test
    void updateResponseDeserializesFromEnvelope() throws Exception {
        CelcoinKycUpdateResponse response = mapper.readValue(
                """
                {"version":"1.0.0","status":"SUCCESS","body":{
                "updateProposalId":"12334dfb-4c4e-43fb-ad93-fa28e3123473",
                "clientCode":"123456","documentNumber":"33333333333",
                "account":"30053913714179"}}""",
                CelcoinKycUpdateResponse.class);

        assertThat(response.updateProposalId()).isEqualTo("12334dfb-4c4e-43fb-ad93-fa28e3123473");
        assertThat(response.account()).isEqualTo("30053913714179");
    }

    @Test
    void webhookSubscriptionRequestSerializesWithOfficialFieldNames() throws Exception {
        CelcoinKycWebhookSubscriptionRequest request = new CelcoinKycWebhookSubscriptionRequest(
                "onboarding-proposal",
                "https://example.com/webhook",
                new CelcoinKycWebhookAuth("login", "password", "basic"));

        String json = mapper.writeValueAsString(request);

        assertThat(json).contains("\"entity\":\"onboarding-proposal\"");
        assertThat(json).contains("\"webhookUrl\":\"https://example.com/webhook\"");
        assertThat(json).contains("\"auth\":{\"login\":\"login\",\"pwd\":\"password\",\"type\":\"basic\"}");
    }

    @Test
    void webhookSubscriptionResponseDeserializesFromEnvelope() throws Exception {
        CelcoinKycWebhookSubscriptionResponse response = mapper.readValue(
                """
                {"version":"1.0.0","status":"SUCCESS","body":{"subscriptionId":"684b45a5391dbba43a367f80"}}""",
                CelcoinKycWebhookSubscriptionResponse.class);

        assertThat(response.subscriptionId()).isEqualTo("684b45a5391dbba43a367f80");
    }

    private CelcoinKycAddress address() {
        return new CelcoinKycAddress("06455030", "Alameda Xingu", "350", "", "Alphaville Industrial", "Barueri", "SP");
    }
}
