package com.brunopedraca.celcoin.onboarding;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class OnboardingDtos {
    private OnboardingDtos() {}

    public record CelcoinKycPersonAccountRequest(
            @NotBlank String document,
            @NotBlank String name,
            LocalDate birthDate,
            @Email String email,
            String phone,
            @Valid CelcoinKycAddress address,
            @Valid CelcoinKycFinancialInformation financialInformation,
            List<CelcoinKycDocument> documents,
            Map<String, Object> metadata) {}

    public record CelcoinKycBusinessAccountRequest(
            @NotBlank String document,
            @NotBlank String legalName,
            String tradeName,
            LocalDate foundationDate,
            @Email String email,
            String phone,
            @Valid CelcoinKycAddress address,
            @Valid CelcoinKycFinancialInformation financialInformation,
            @NotEmpty List<@Valid CelcoinKycBusinessOwner> owners,
            List<CelcoinKycDocument> documents,
            Map<String, Object> metadata) {}

    public record CelcoinKycFinancialInformation(
            BigDecimal monthlyIncome,
            BigDecimal monthlyRevenue,
            BigDecimal declaredAssets,
            String occupation,
            String businessActivity,
            String sourceOfFunds,
            Boolean politicallyExposedPerson) {}

    public record CelcoinKycAddress(
            @NotBlank String street,
            @NotBlank String number,
            String complement,
            @NotBlank String neighborhood,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String postalCode,
            String country) {}

    public record CelcoinKycBusinessOwner(
            @NotBlank String document,
            @NotBlank String name,
            LocalDate birthDate,
            @PositiveOrZero BigDecimal ownershipPercentage,
            @Email String email,
            String phone,
            @Valid CelcoinKycAddress address,
            @Valid CelcoinKycFinancialInformation financialInformation,
            List<CelcoinKycDocument> documents) {}

    public record CelcoinKycDocument(
            @NotBlank String type,
            @NotBlank String fileName,
            @NotBlank String contentType,
            @NotBlank String contentBase64,
            String side,
            Map<String, Object> metadata) {}

    public record CelcoinKycFinancialInformationRequest(
            @NotBlank String onboardingId, @NotNull @Valid CelcoinKycFinancialInformation financialInformation) {}

    public record CelcoinKycWebhookSubscriptionRequest(
            @NotBlank String eventType, @NotBlank String url, String secret, Map<String, Object> metadata) {}

    public record CelcoinKycWebhookSubscriptionResponse(
            String subscriptionId, String eventType, String url, String status, Map<String, Object> raw) {}

    public record CelcoinKycOnboardingResponse(
            String onboardingId,
            String accountId,
            String document,
            String type,
            String status,
            OffsetDateTime createdAt,
            Map<String, Object> raw) {}

    public record CelcoinKycStatusResponse(
            String onboardingId,
            String accountId,
            String status,
            String reason,
            OffsetDateTime updatedAt,
            Map<String, Object> raw) {}

    public record CelcoinKycWebhookEvent(
            String eventId,
            String eventType,
            String onboardingId,
            String accountId,
            String status,
            OffsetDateTime occurredAt,
            Map<String, Object> raw) {}
}
