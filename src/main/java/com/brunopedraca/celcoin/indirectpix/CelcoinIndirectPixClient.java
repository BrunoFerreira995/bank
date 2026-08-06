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
    public Map<String, Object> listKeys(CelcoinIndirectDictKeyListRequest request) {
        ensureConfigured();
        if (request == null) throw new IllegalArgumentException("key list request is required");
        requireText(request.accountType(), "accountType");
        requireText(request.account(), "account");
        requireText(request.taxId(), "taxId");
        Map<String, Object> body = new HashMap<>();
        body.put("branch", request.branch());
        body.put("accountType", request.accountType());
        body.put("account", request.account());
        body.put("taxId", request.taxId());
        return httpClient.post(BASE + "/dict/entry/list", body, Map.class, context(null));
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

    @Override
    public Map<String, Object> createFundsRecovery(CelcoinFundsRecoveryRequest request, String idempotencyKey) {
        ensureConfigured();
        validateRecovery(request);
        return httpClient.post(BASE + "/funds-recovery", request, Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> getFundsRecovery(String fundsRecoveryId) {
        ensureConfigured();
        requireText(fundsRecoveryId, "fundsRecoveryId");
        return httpClient.get(BASE + "/funds-recovery/" + encode(fundsRecoveryId), Map.class, context(null));
    }

    @Override
    public Map<String, Object> cancelFundsRecovery(String fundsRecoveryId, String idempotencyKey) {
        ensureConfigured();
        requireText(fundsRecoveryId, "fundsRecoveryId");
        return httpClient.post(BASE + "/funds-recovery/" + encode(fundsRecoveryId) + "/cancel", Map.of(), Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> getFundsRecoveryGraph(String fundsRecoveryId) {
        ensureConfigured();
        requireText(fundsRecoveryId, "fundsRecoveryId");
        return httpClient.get(BASE + "/funds-recovery/" + encode(fundsRecoveryId) + "/tracking-graph", Map.class,
                context(null));
    }

    @Override
    public Map<String, Object> updateFundsRecovery(
            String fundsRecoveryId, CelcoinFundsRecoveryUpdateRequest request, String idempotencyKey) {
        ensureConfigured();
        requireText(fundsRecoveryId, "fundsRecoveryId");
        if (request == null) throw new IllegalArgumentException("funds recovery update is required");
        return httpClient.put(BASE + "/funds-recovery/" + encode(fundsRecoveryId), request, Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createPayment(Map<String, Object> request, String idempotencyKey) {
        ensureConfigured();
        requireBody(request, "payment request");
        return httpClient.post(BASE + "/payment", request, Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> getPaymentStatus(Map<String, Object> query) {
        ensureConfigured();
        return httpClient.get(BASE + "/payment/pi/status" + suffix(query), Map.class, context(null));
    }

    @Override
    public Map<String, Object> getReceivementStatus(Map<String, Object> query) {
        ensureConfigured();
        return httpClient.get(BASE + "/receivement/status" + suffix(query), Map.class, context(null));
    }

    @Override
    public Map<String, Object> reverseReceivement(String endToEndId, Map<String, Object> request,
            String idempotencyKey) {
        ensureConfigured();
        requireText(endToEndId, "endToEndId");
        return httpClient.post(BASE + "/reverse/pi/endtoend/" + encode(endToEndId),
                request == null ? Map.of() : request, Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createInternalReport(Map<String, Object> request, String idempotencyKey) {
        ensureConfigured();
        requireBody(request, "internal report request");
        return httpClient.post(BASE + "/payment/internal-report", request, Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createStaticQrCode(Map<String, Object> request, String idempotencyKey) {
        ensureConfigured();
        requireBody(request, "static QR Code request");
        return httpClient.post(BASE + "/brcode/static", request, Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createDynamicQrCode(Map<String, Object> request, String idempotencyKey) {
        ensureConfigured();
        requireBody(request, "dynamic QR Code request");
        return httpClient.post(BASE + "/brcode/dynamic", request, Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> decodeDynamicQrCode(String encodedUrl) {
        ensureConfigured();
        requireText(encodedUrl, "encodedUrl");
        return httpClient.post(BASE + "/brcode/dynamic/payload?url=" + encode(encodedUrl),
                Map.of(), Map.class, context(null));
    }

    @Override
    public CelcoinIndirectCashInAuthorizationResponse parseCashInAuthorization(Map<String, Object> payload) {
        requireBody(payload, "cash-in authorization payload");
        return new CelcoinIndirectCashInAuthorizationResponse(
                text(payload.get("Status"), payload.get("status")),
                text(payload.get("ApprovalId"), payload.get("approvalId")),
                text(payload.get("ReasonPhrase"), payload.get("reasonPhrase")),
                text(payload.get("ReasonCode"), payload.get("reasonCode")),
                text(payload.get("Reason"), payload.get("reason")));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CelcoinIndirectWebhookEvent parseWebhook(Map<String, Object> payload) {
        requireBody(payload, "webhook payload");
        return new CelcoinIndirectWebhookEvent(
                text(payload.get("entity"), payload.get("Entity")),
                text(payload.get("status"), payload.get("Status")),
                map(payload.get("body"), payload.get("RequestBody")),
                map(payload.get("error"), payload.get("Error")),
                payload);
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

    private static void requireText(String value, String name) {
        if (!StringUtils.hasText(value)) throw new IllegalArgumentException(name + " is required");
    }

    private static void requireBody(Map<String, Object> value, String name) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException(name + " is required");
    }

    private static String suffix(Map<String, Object> values) {
        String query = query(values);
        return query.isEmpty() ? "" : "?" + query;
    }

    private static String text(Object first, Object second) {
        Object value = first != null ? first : second;
        return value == null ? null : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object first, Object second) {
        Object value = first != null ? first : second;
        return value instanceof Map<?, ?> ? (Map<String, Object>) value : Map.of();
    }

    private static void validateRecovery(CelcoinFundsRecoveryRequest request) {
        if (request == null) throw new IllegalArgumentException("funds recovery request is required");
        requireText(request.flowType(), "flowType");
        requireText(request.rootEndToEnd(), "rootEndToEnd");
        if (request.rootEndToEnd().length() != 32) throw new IllegalArgumentException("rootEndToEnd must have 32 characters");
        requireText(request.situationType(), "situationType");
        if ("OTHER".equals(request.situationType()) && !StringUtils.hasText(request.reportDetails()))
            throw new IllegalArgumentException("reportDetails is required for situationType OTHER");
        if (request.contactInformation() == null || !StringUtils.hasText(request.contactInformation().email())
                || !StringUtils.hasText(request.contactInformation().phone()))
            throw new IllegalArgumentException("contactInformation email and phone are required");
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
