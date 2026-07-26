package com.brunopedraca.celcoin.onboarding;

import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycFinancialInformationRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycOnboardingResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycStatusResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionResponse;

public interface CelcoinOnboardingOperations {
    default CelcoinKycOnboardingResponse createPersonAccount(CelcoinKycPersonAccountRequest request) {
        return createPersonAccount(request, null);
    }

    CelcoinKycOnboardingResponse createPersonAccount(CelcoinKycPersonAccountRequest request, String idempotencyKey);

    default CelcoinKycOnboardingResponse createBusinessAccount(CelcoinKycBusinessAccountRequest request) {
        return createBusinessAccount(request, null);
    }

    CelcoinKycOnboardingResponse createBusinessAccount(CelcoinKycBusinessAccountRequest request, String idempotencyKey);

    CelcoinKycStatusResponse getStatus(String onboardingId);

    CelcoinKycOnboardingResponse updateFinancialInformation(CelcoinKycFinancialInformationRequest request);

    CelcoinKycWebhookSubscriptionResponse createWebhookSubscription(CelcoinKycWebhookSubscriptionRequest request);

    CelcoinKycStatusResponse simulateStatus(String onboardingId, String status);
}
