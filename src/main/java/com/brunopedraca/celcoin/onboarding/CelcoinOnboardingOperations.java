package com.brunopedraca.celcoin.onboarding;

import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycOnboardingResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountUpdateRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycProposalSearchResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycUpdateResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinBiometricAuthRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinBiometricAuthResponse;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinBiometricFilesResponse;
import java.util.Map;

public interface CelcoinOnboardingOperations {
    default CelcoinKycOnboardingResponse createPersonAccount(CelcoinKycPersonAccountRequest request) {
        return createPersonAccount(request, null);
    }

    CelcoinKycOnboardingResponse createPersonAccount(CelcoinKycPersonAccountRequest request, String idempotencyKey);

    default CelcoinKycOnboardingResponse createBusinessAccount(CelcoinKycBusinessAccountRequest request) {
        return createBusinessAccount(request, null);
    }

    CelcoinKycOnboardingResponse createBusinessAccount(CelcoinKycBusinessAccountRequest request, String idempotencyKey);

    CelcoinKycProposalSearchResponse getProposal(String proposalId);

    CelcoinKycUpdateResponse updatePersonAccount(String account, CelcoinKycPersonAccountUpdateRequest request);

    CelcoinKycWebhookSubscriptionResponse createWebhookSubscription(CelcoinKycWebhookSubscriptionRequest request);

    CelcoinBiometricAuthResponse createBiometricAuthentication(CelcoinBiometricAuthRequest request);

    CelcoinBiometricAuthResponse listBiometricAuthentications(Map<String, Object> filters);

    CelcoinBiometricFilesResponse getBiometricFiles(String biometricAuthId, String clientCode);
}
