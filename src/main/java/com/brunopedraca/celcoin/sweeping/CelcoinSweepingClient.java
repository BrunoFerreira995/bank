package com.brunopedraca.celcoin.sweeping;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.sweeping.SweepingDtos.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class CelcoinSweepingClient implements CelcoinSweepingOperations {
    private static final String BASE = "/baas/v1/open/itp/sweeping-accounts";
    private final CelcoinHttpClient httpClient;

    public CelcoinSweepingClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CelcoinSweepingBrandListResponse listBrands() {
        ensureConfigured();
        CelcoinSweepingBrand[] brands = httpClient.get(
                "/baas/v1/open/itp/participants/brands?type=PAYMENT",
                CelcoinSweepingBrand[].class,
                context(null));
        return new CelcoinSweepingBrandListResponse(brands == null ? List.of() : List.of(brands), null);
    }

    @Override
    public CelcoinSweepingConsentResponse createConsent(
            CelcoinSweepingConsentRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> data = new HashMap<>();
        data.put("creditors", request.creditors());
        data.put("loggedUser", Map.of("document", Map.of(
                "identification", request.loggedUserDocument(), "rel", "CPF")));
        data.put("recurringConfiguration", Map.of("sweeping", request.sweepingConfiguration()));
        Map<String, Object> body = new HashMap<>();
        body.put("brandId", request.brandId());
        body.put("redirectUrl", request.redirectUrl());
        body.put("data", data);
        if (request.metadata() != null) body.putAll(request.metadata());
        return httpClient.post(BASE + "/payment-initiation", body,
                CelcoinSweepingConsentResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinSweepingCallbackResponse processCallback(CelcoinSweepingCallbackRequest request) {
        ensureConfigured();
        return httpClient.post("/baas/v1/open/itp/payment-initiation/callback", Map.of(
                        "code", request.code(), "state", request.state(), "id_token", request.idToken()),
                CelcoinSweepingCallbackResponse.class, context(null));
    }

    @Override
    public CelcoinSweepingConsentResponse cancelConsent(
            String paymentInitiationId, CelcoinSweepingCancelRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> body = Map.of("data", Map.of(
                "status", "CANC",
                "cancellation", Map.of("cancelledBy", Map.of("document", Map.of(
                        "identification", request.cancelledByDocument(), "rel", "CPF")))));
        return httpClient.patch(BASE + "/payment-initiation/" + encode(paymentInitiationId), body,
                CelcoinSweepingConsentResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinSweepingConsentListResponse listConsents(CelcoinSweepingConsentListRequest request) {
        ensureConfigured();
        String path = BASE + "/payment-initiation?" + query()
                .param("status", request.status())
                .param("initialDate", request.initialDate())
                .param("finalDate", request.finalDate())
                .param("page", request.page())
                .param("pageSize", request.pageSize());
        return httpClient.get(path, CelcoinSweepingConsentListResponse.class, context(null));
    }

    @Override
    public CelcoinSweepingConsentResponse getConsent(String paymentInitiationId) {
        ensureConfigured();
        return httpClient.get(BASE + "/payment-initiation/" + encode(paymentInitiationId),
                CelcoinSweepingConsentResponse.class, context(null));
    }

    @Override
    public CelcoinSweepingPaymentResponse createPayment(
            CelcoinSweepingPaymentRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> data = new HashMap<>();
        data.put("date", request.date());
        data.put("payment", Map.of("amount", request.amount().toPlainString(), "currency", "BRL"));
        data.put("creditorAccount", request.creditorAccount());
        if (StringUtils.hasText(request.remittanceInformation())) {
            data.put("remittanceInformation", request.remittanceInformation());
        }
        if (StringUtils.hasText(request.ibgeTownCode())) data.put("ibgeTownCode", request.ibgeTownCode());
        if (request.riskSignals() != null) data.put("riskSignals", request.riskSignals());
        return httpClient.post(BASE + "/payment-initiation/" + encode(request.paymentInitiationId()) + "/payments",
                Map.of("data", data), CelcoinSweepingPaymentResponse.class, context(idempotencyKey));
    }

    private void ensureConfigured() {
        if (httpClient == null) throw new CelcoinIntegrationException(
                "Celcoin Sweeping Accounts endpoint path is not configured because the official contract was not provided");
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private QueryBuilder query() { return new QueryBuilder(); }

    private static String encode(String value) {
        return StringUtils.hasText(value) ? URLEncoder.encode(value, StandardCharsets.UTF_8) : "";
    }

    private static final class QueryBuilder {
        private final StringBuilder value = new StringBuilder();
        QueryBuilder param(String name, Object parameter) {
            if (parameter != null && StringUtils.hasText(String.valueOf(parameter))) {
                if (!value.isEmpty()) value.append('&');
                value.append(name).append('=').append(encode(String.valueOf(parameter)));
            }
            return this;
        }
        @Override public String toString() { return value.toString(); }
    }
}
