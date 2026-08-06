package com.brunopedraca.celcoin.onboarding;

import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
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
import org.springframework.util.StringUtils;

public class CelcoinOnboardingClient implements CelcoinOnboardingOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinOnboardingClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CelcoinKycOnboardingResponse createPersonAccount(
            CelcoinKycPersonAccountRequest request, String idempotencyKey) {
        return httpClient.post(
                "/onboarding/v1/onboarding-proposal/natural-person",
                request,
                CelcoinKycOnboardingResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinKycOnboardingResponse createBusinessAccount(
            CelcoinKycBusinessAccountRequest request, String idempotencyKey) {
        return httpClient.post(
                "/onboarding/v1/onboarding-proposal/legal-person",
                request,
                CelcoinKycOnboardingResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinKycProposalSearchResponse getProposal(String proposalId) {
        String path = "/onboarding/v1/onboarding-proposal?"
                + query().param("ProposalId", proposalId).build();
        return httpClient.get(path, CelcoinKycProposalSearchResponse.class, context(null));
    }

    @Override
    public CelcoinKycUpdateResponse updatePersonAccount(String account, CelcoinKycPersonAccountUpdateRequest request) {
        return httpClient.put(
                "/onboarding/v1/onboarding-proposal/account/" + encode(account) + "/natural-person",
                request,
                CelcoinKycUpdateResponse.class,
                context(null));
    }

    @Override
    public CelcoinKycWebhookSubscriptionResponse createWebhookSubscription(
            CelcoinKycWebhookSubscriptionRequest request) {
        return httpClient.post(
                "/baas/v2/webhook/subscription", request, CelcoinKycWebhookSubscriptionResponse.class, context(null));
    }

    @Override
    public CelcoinBiometricAuthResponse createBiometricAuthentication(CelcoinBiometricAuthRequest request) {
        return httpClient.post("/onboarding/v1/biometric-auth", request,
                CelcoinBiometricAuthResponse.class, context(null));
    }

    @Override
    public CelcoinBiometricAuthResponse listBiometricAuthentications(Map<String, Object> filters) {
        StringBuilder path = new StringBuilder("/onboarding/v1/biometric-auth?");
        if (filters != null) filters.forEach((key, value) -> {
            if (value != null) {
                if (path.charAt(path.length() - 1) != '?') path.append('&');
                path.append(encode(key)).append('=').append(encode(String.valueOf(value)));
            }
        });
        return httpClient.get(path.toString(), CelcoinBiometricAuthResponse.class, context(null));
    }

    @Override
    public CelcoinBiometricFilesResponse getBiometricFiles(String biometricAuthId, String clientCode) {
        StringBuilder path = new StringBuilder("/onboarding/v1/biometric-auth/files?");
        if (StringUtils.hasText(biometricAuthId)) path.append("biometricAuthId=").append(encode(biometricAuthId));
        if (StringUtils.hasText(clientCode)) {
            if (path.charAt(path.length() - 1) != '?') path.append('&');
            path.append("clientCode=").append(encode(clientCode));
        }
        return httpClient.get(path.toString(), CelcoinBiometricFilesResponse.class, context(null));
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private static String encode(String value) {
        return StringUtils.hasText(value)
                ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8)
                : "";
    }

    private QueryBuilder query() {
        return new QueryBuilder();
    }

    private static final class QueryBuilder {
        private final StringBuilder sb = new StringBuilder();

        QueryBuilder param(String name, String value) {
            if (StringUtils.hasText(value)) {
                if (!sb.isEmpty()) {
                    sb.append('&');
                }
                sb.append(name).append('=').append(encode(value));
            }
            return this;
        }

        String build() {
            return sb.toString();
        }
    }
}
