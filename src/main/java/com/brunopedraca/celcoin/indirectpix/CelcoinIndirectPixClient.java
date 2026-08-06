package com.brunopedraca.celcoin.indirectpix;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.indirectpix.IndirectPixDtos.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class CelcoinIndirectPixClient implements CelcoinIndirectPixOperations {
    private static final String BASE = "/pix-indirect/v1";
    private final CelcoinHttpClient httpClient;

    public CelcoinIndirectPixClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Map<String, Object> lookupKey(CelcoinIndirectDictLookupRequest request) {
        ensureConfigured();
        String path = BASE + "/dict/v2/key?" + query().param("payerId", request.payerId())
                .param("key", request.key()).param("endToEndId", request.endToEndId());
        return httpClient.post(path, Map.of(), Map.class,
                new CelcoinRequestContext(null, null));
    }

    @Override
    public Map<String, Object> checkKeys(CelcoinIndirectDictKeyCheckRequest request) {
        ensureConfigured();
        return httpClient.post(BASE + "/dict/keychecker", request, Map.class, context(null));
    }

    @Override
    public Map<String, Object> createKey(CelcoinIndirectDictKeyRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BASE + "/dict/entry", request.payload(), Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> deleteKey(CelcoinIndirectDictDeleteRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.delete(BASE + "/dict/entry/" + encode(request.key()), request.payload(),
                Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createClaim(CelcoinIndirectClaimRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BASE + "/dict/claim", request.payload(), Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> getClaim(String claimId) {
        ensureConfigured();
        return httpClient.get(BASE + "/dict/claim/" + encode(claimId), Map.class, context(null));
    }

    @Override
    public Map<String, Object> listClaims(Map<String, Object> query) {
        ensureConfigured();
        return httpClient.get(BASE + "/dict/claim/list?" + query(query), Map.class, context(null));
    }

    @Override
    public Map<String, Object> createInfraction(CelcoinIndirectInfractionRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BASE + "/infraction/infraction-report", request.payload(), Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> getInfraction(String infractionId) {
        ensureConfigured();
        return httpClient.get(BASE + "/infraction/infraction-report/" + encode(infractionId), Map.class, context(null));
    }

    @Override
    public Map<String, Object> listInfractions(CelcoinIndirectInfractionListRequest request) {
        ensureConfigured();
        return httpClient.get(BASE + "/infraction/list?" + query()
                        .param("isReporter", request.isReporter()).param("isCounterparty", request.isCounterparty())
                        .param("fundsRecoveryId", request.fundsRecoveryId()).param("status", request.status())
                        .param("dateHourChangeStart", request.dateHourChangeStart())
                        .param("dateHourChangeEnd", request.dateHourChangeEnd())
                        .param("pageSize", request.pageSize()).param("page", request.page()),
                Map.class, context(null));
    }

    @Override
    public Map<String, Object> closeInfraction(
            String infractionId, CelcoinIndirectInfractionCloseRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BASE + "/infraction/" + encode(infractionId) + "/close", request.payload(),
                Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createMedRefund(CelcoinIndirectMedRefundRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BASE + "/med/refund", request.payload(), Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> getMedRefund(String refundId) {
        ensureConfigured();
        return httpClient.get(BASE + "/med/refund/" + encode(refundId), Map.class, context(null));
    }

    @Override
    public Map<String, Object> cancelMedRefund(String refundId, String reason, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BASE + "/med/refund/" + encode(refundId) + "/cancel",
                Map.of("reason", reason == null ? "USER_REQUESTED" : reason), Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> closeMedRefund(
            String refundId, CelcoinIndirectMedCloseRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("transactionId", request.transactionId());
        body.put("refundAmount", request.refundAmount());
        body.put("refundAnalysisResult", request.refundAnalysisResult());
        body.put("refundAnalysisDetails", request.refundAnalysisDetails());
        body.put("refundRejectionReason", request.refundRejectionReason());
        return httpClient.post(BASE + "/med/refund/" + encode(refundId) + "/close", body,
                Map.class, context(idempotencyKey));
    }

    private void ensureConfigured() {
        if (httpClient == null) throw new CelcoinIntegrationException(
                "Celcoin Pix Indirect endpoint path is not configured because the official contract was not provided");
    }

    private CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }

    private QueryBuilder query() { return new QueryBuilder(); }

    private static String encode(String value) {
        return StringUtils.hasText(value) ? URLEncoder.encode(value, StandardCharsets.UTF_8) : "";
    }

    private static String query(Map<String, Object> values) {
        QueryBuilder builder = new QueryBuilder();
        if (values != null) values.forEach(builder::param);
        return builder.toString();
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
