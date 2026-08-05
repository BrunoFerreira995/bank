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
