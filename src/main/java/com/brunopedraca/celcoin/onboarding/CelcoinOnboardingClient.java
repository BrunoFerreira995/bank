package com.brunopedraca.celcoin.onboarding;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycFinancialInformationRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycOnboardingResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycStatusResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionResponse;

public class CelcoinOnboardingClient implements CelcoinOnboardingOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinOnboardingClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinKycOnboardingResponse createPersonAccount(
            CelcoinKycPersonAccountRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinKycOnboardingResponse createBusinessAccount(
            CelcoinKycBusinessAccountRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinKycStatusResponse getStatus(String onboardingId) {
        throw unspecified();
    }

    public CelcoinKycOnboardingResponse updateFinancialInformation(CelcoinKycFinancialInformationRequest request) {
        throw unspecified();
    }

    public CelcoinKycWebhookSubscriptionResponse createWebhookSubscription(
            CelcoinKycWebhookSubscriptionRequest request) {
        throw unspecified();
    }

    public CelcoinKycStatusResponse simulateStatus(String onboardingId, String status) {
        throw unspecified();
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin onboarding KYC endpoint path is not configured because the official contract was not provided in this first version");
    }
}
