package com.brunopedraca.celcoin.onboarding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public final class OnboardingDtos {
    private OnboardingDtos() {}

    // ===================== Pessoa Física (PF) =====================

    /** Endereço. Campos mapeados para {@code AddressRequest} da Celcoin. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycAddress(
            @NotBlank String postalCode,
            @NotBlank String street,
            @NotBlank String number,
            String addressComplement,
            @NotBlank String neighborhood,
            @NotBlank String city,
            @NotBlank String state) {}

    /** Documento enviado via URL pública. Campos mapeados para {@code FileRequest} da Celcoin. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycDocument(@NotBlank String type, @NotBlank String data) {}

    /** Informações financeiras do titular PF. Códigos de enum da Celcoin (ex.: "1DINP02"). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycFinancialDetails(
            String declaredIncome, String presumedIncome, String occupation, String netWorth) {}

    /** Proposta de criação de conta Pessoa Física — {@code POST /onboarding/v1/onboarding-proposal/natural-person}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycPersonAccountRequest(
            @NotBlank String clientCode,
            @NotBlank String documentNumber,
            @NotBlank String phoneNumber,
            @NotBlank @Email String email,
            @NotBlank String motherName,
            @NotBlank String fullName,
            String socialName,
            @NotBlank String birthDate,
            @NotNull @Valid CelcoinKycAddress address,
            Boolean isPoliticallyExposedPerson,
            String onboardingType,
            List<@Valid CelcoinKycDocument> files,
            @Valid CelcoinKycFinancialDetails financialDetails) {

        public CelcoinKycPersonAccountRequest {
            if (onboardingType == null) {
                onboardingType = "BAAS";
            }
            if (isPoliticallyExposedPerson == null) {
                isPoliticallyExposedPerson = false;
            }
        }
    }

    // ===================== Pessoa Jurídica (PJ) =====================

    /** Detalhes financeiros de um sócio/representante. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycOwnerFinancialDetails(
            String ownerDeclaredIncome,
            String ownerPresumedIncome,
            String ownerDeclaredRevenue,
            String ownerPresumedRevenue) {}

    /** Sócio, representante ou demais sócios da empresa. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycBusinessOwner(
            @NotBlank String ownerType,
            @NotBlank String documentNumber,
            @NotBlank String fullName,
            @NotBlank String phoneNumber,
            @NotBlank @Email String email,
            String motherName,
            String socialName,
            @NotBlank String birthDate,
            @NotNull @Valid CelcoinKycAddress address,
            Boolean isPoliticallyExposedPerson,
            @Valid CelcoinKycOwnerFinancialDetails financialOwnerDetails) {

        public CelcoinKycBusinessOwner {
            if (isPoliticallyExposedPerson == null) {
                isPoliticallyExposedPerson = false;
            }
        }
    }

    /** Informações financeiras da empresa. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycCompanyFinancialDetails(String declaredCompanyRevenue, String presumedCompanyRevenue) {}

    /** Proposta de criação de conta Pessoa Jurídica — {@code POST /onboarding/v1/onboarding-proposal/legal-person}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycBusinessAccountRequest(
            @NotBlank String clientCode,
            @NotBlank String contactNumber,
            @NotBlank String documentNumber,
            @NotBlank @Email String businessEmail,
            @NotBlank String businessName,
            @NotBlank String tradingName,
            String companyType,
            @NotEmpty List<@Valid CelcoinKycBusinessOwner> owner,
            @NotNull @Valid CelcoinKycAddress businessAddress,
            String onboardingType,
            List<@Valid CelcoinKycDocument> files,
            @Valid CelcoinKycCompanyFinancialDetails financialCompanyDetails) {

        public CelcoinKycBusinessAccountRequest {
            if (companyType == null) {
                companyType = "PJ";
            }
            if (onboardingType == null) {
                onboardingType = "BAAS";
            }
        }
    }

    // ===================== Respostas =====================

    /** Resposta de criação de proposta (PF/PJ). Envelope {@code {version, status, body}}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycOnboardingResponse(String version, String status, CelcoinKycOnboardingBody body) {
        public String proposalId() {
            return body == null ? null : body.proposalId();
        }

        public String clientCode() {
            return body == null ? null : body.clientCode();
        }

        public String documentNumber() {
            return body == null ? null : body.documentNumber();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinKycOnboardingBody(String proposalId, String clientCode, String documentNumber) {}
    }

    /** Item de proposta retornado na consulta. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycProposal(
            String proposalId,
            String clientCode,
            String documentNumber,
            String status,
            String proposalType,
            List<CelcoinKycDocumentscopy> documentscopys) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycDocumentscopy(String createdAt) {}

    /** Resposta da consulta de propostas — {@code GET /onboarding/v1/onboarding-proposal}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycProposalSearchResponse(String version, String status, CelcoinKycProposalSearchBody body) {
        public List<CelcoinKycProposal> proposals() {
            return body == null || body.proposal() == null ? List.of() : body.proposal();
        }

        public Integer totalItems() {
            return body == null ? null : body.totalItems();
        }

        public Integer totalPages() {
            return body == null ? null : body.totalPages();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinKycProposalSearchBody(
                Integer limit,
                Integer currentPage,
                Integer limitPerPage,
                Integer totalPages,
                Integer totalItems,
                List<CelcoinKycProposal> proposal) {}
    }

    /** Resposta da atualização cadastral PF — {@code PUT /onboarding/v1/onboarding-proposal/account/{account}/natural-person}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycUpdateResponse(String version, String status, CelcoinKycUpdateBody body) {
        public String updateProposalId() {
            return body == null ? null : body.updateProposalId();
        }

        public String account() {
            return body == null ? null : body.account();
        }

        public String documentNumber() {
            return body == null ? null : body.documentNumber();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinKycUpdateBody(
                String updateProposalId, String clientCode, String documentNumber, String account) {}
    }

    // ===================== Atualização cadastral (PF) =====================

    /** Requisição de atualização cadastral PF — campos mapeados para {@code NaturalPersonUpdateRequest}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycPersonAccountUpdateRequest(
            @NotBlank String clientCode,
            @NotBlank String phoneNumber,
            @NotBlank @Email String email,
            @NotBlank String motherName,
            @NotBlank String fullName,
            String socialName,
            @NotNull @Valid CelcoinKycAddress address) {}

    // ===================== Webhooks =====================

    /** Cadastro de webhook BaaS — {@code POST /baas/v2/webhook/subscription}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycWebhookSubscriptionRequest(
            @NotBlank String entity, @NotBlank String webhookUrl, CelcoinKycWebhookAuth auth) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycWebhookAuth(String login, String pwd, String type) {}

    /** Resposta do cadastro de webhook. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycWebhookSubscriptionResponse(
            String version, String status, CelcoinKycWebhookSubscriptionBody body) {
        public String subscriptionId() {
            return body == null ? null : body.subscriptionId();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinKycWebhookSubscriptionBody(String subscriptionId) {}
    }

    /** Payload genérico dos webhooks de onboarding/KYC. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinKycWebhookEvent(
            String entity, String status, String createTimestamp, String webhookId, Map<String, Object> body) {}
}
