package com.brunopedraca.celcoin.pixauto;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.pixauto.PixAutoDtos.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public class CelcoinPixAutoClient implements CelcoinPixAutoOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinPixAutoClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinPixAutoConsentResponse createConsent(CelcoinPixAutoConsentRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> metadata = metadata(request.metadata());
        Map<String, Object> automatic = new HashMap<>();
        automatic.put("contractId", required(metadata, "contractId"));
        automatic.put("interval", request.frequency());
        automatic.put("isRetryAccepted", metadata.getOrDefault("isRetryAccepted", Boolean.FALSE));
        automatic.put("referenceStartDate", required(metadata, "referenceStartDate"));
        Object fixedAmount = metadata.get("fixedAmount");
        putAmount(automatic, "fixedAmount", fixedAmount);
        if (fixedAmount == null) {
            putAmount(automatic, "minimumVariableAmount", metadata.get("minimumVariableAmount"));
            putAmount(automatic, "maximumVariableAmount",
                    metadata.containsKey("maximumVariableAmount")
                            ? metadata.get("maximumVariableAmount")
                            : request.maxAmount());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("startDateTime", required(metadata, "startDateTime"));
        data.put("creditors", List.of(Map.of(
                "name", metadata.getOrDefault("creditorName", request.payerName()),
                "cpfCnpj", metadata.getOrDefault("creditorDocument", request.payerDocument()),
                "personType", metadata.getOrDefault("personType", "PESSOA_NATURAL"))));
        data.put("loggedUser", Map.of("document", Map.of(
                "identification", request.payerDocument(), "rel", "CPF")));
        data.put("recurringConfiguration", Map.of("automatic", automatic));
        if (metadata.containsKey("contractDebtor")) data.put("contractDebtor", metadata.get("contractDebtor"));
        Map<String, Object> body = Map.of(
                "brandId", required(metadata, "brandId"),
                "redirectUrl", required(metadata, "redirectUrl"),
                "data", data);
        return httpClient.post("/baas/v1/open/itp/automatic-payments/payment-initiation", body,
                CelcoinPixAutoConsentResponse.class, context(idempotencyKey));
    }

    public CelcoinPixAutoConsentStatusResponse getConsentStatus(String consentId) {
        ensureConfigured();
        return httpClient.get("/baas/v1/open/itp/automatic-payments/payment-initiation?"
                        + query().param("id", consentId),
                CelcoinPixAutoConsentStatusResponse.class, context(null));
    }

    public CelcoinPixAutoConsentListResponse listConsents(CelcoinPixAutoListRequest request) {
        ensureConfigured();
        String path = "/baas/v1/open/itp/automatic-payments/payment-initiation?"
                + query().param("accountId", request.accountId()).param("status", request.status())
                        .param("startAt", request.startAt()).param("endAt", request.endAt())
                        .param("page", request.page()).param("limit", request.size());
        return httpClient.get(path, CelcoinPixAutoConsentListResponse.class, context(null));
    }

    public CelcoinPixAutoConsentStatusResponse cancelConsent(String consentId, String idempotencyKey) {
        ensureConfigured();
        return httpClient.patch("/baas/v1/open/itp/automatic-payments/v2/payment-initiation/"
                        + encode(consentId), Map.of("status", "REVOKED"),
                CelcoinPixAutoConsentStatusResponse.class, context(idempotencyKey));
    }

    public CelcoinPixAutoScheduleResponse schedule(CelcoinPixAutoScheduleRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> payment = new HashMap<>();
        payment.put("amount", request.amount());
        payment.put("currency", "BRL");
        Map<String, Object> data = new HashMap<>();
        data.put("date", request.startDate());
        data.put("payment", payment);
        if (request.metadata() != null) data.putAll(request.metadata());
        return httpClient.post("/baas/v1/open/itp/automatic-payments/payment-initiation/"
                        + encode(request.consentId()) + "/payments", Map.of("data", data),
                CelcoinPixAutoScheduleResponse.class, context(idempotencyKey));
    }

    public CelcoinPixAutoScheduleStatusResponse getScheduleStatus(String scheduleId) {
        ensureConfigured();
        return httpClient.get("/baas/v1/open/itp/automatic-payments/payment-initiation?"
                        + query().param("paymentId", scheduleId),
                CelcoinPixAutoScheduleStatusResponse.class, context(null));
    }

    public CelcoinPixAutoScheduleListResponse listSchedules(CelcoinPixAutoListRequest request) {
        ensureConfigured();
        String path = "/baas/v1/open/itp/automatic-payments/payment-initiation?"
                + query().param("page", request.page()).param("limit", request.size())
                        .param("accountId", request.accountId()).param("status", request.status());
        return httpClient.get(path, CelcoinPixAutoScheduleListResponse.class, context(null));
    }

    public CelcoinPixAutoCancelResponse cancelSchedule(String scheduleId, String idempotencyKey) {
        ensureConfigured();
        return httpClient.patch("/baas/v1/open/itp/automatic-payments/v2/payment-initiation/"
                        + encode(scheduleId), Map.of("status", "CANCELLED"),
                CelcoinPixAutoCancelResponse.class, context(idempotencyKey));
    }

    public CelcoinPixAutoLiquidationResponse getLiquidation(String scheduleId) {
        ensureConfigured();
        return httpClient.get("/baas/v1/open/itp/automatic-payments/payment-initiation?"
                        + query().param("paymentId", scheduleId),
                CelcoinPixAutoLiquidationResponse.class, context(null));
    }

    public CelcoinPixAutoScheduleResponse createReceiveSchedule(
            CelcoinPixAutoReceiveScheduleRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("date", request.startDate());
        body.put("payment", Map.of("amount", request.amount(), "currency", "BRL"));
        if (request.metadata() != null) body.putAll(request.metadata());
        return httpClient.post("/baas/v1/open/itp/automatic-payments/payment-initiation/"
                        + encode(request.consentId()) + "/payments", Map.of("data", body),
                CelcoinPixAutoScheduleResponse.class, context(idempotencyKey));
    }

    public CelcoinPixAutoLiquidationResponse retryReceipt(CelcoinPixAutoRetryRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("attempt", request.attempt());
        body.put("reason", request.reason());
        if (request.metadata() != null) body.putAll(request.metadata());
        return httpClient.post("/automatic-payments/v2/pix/recurring-payments/"
                        + encode(request.scheduleId()) + "/retry", body,
                CelcoinPixAutoLiquidationResponse.class, context(idempotencyKey));
    }

    public CelcoinPixAutoCancelResponse cancelRecurrence(String recurrenceId, String idempotencyKey) {
        ensureConfigured();
        return httpClient.patch("/baas/v1/open/itp/automatic-payments/v2/payment-initiation/"
                        + encode(recurrenceId), Map.of("status", "REVOKED"),
                CelcoinPixAutoCancelResponse.class, context(idempotencyKey));
    }

    public List<CelcoinPixAutoRejectionReason> rejectionReasons() {
        return List.of(
                new CelcoinPixAutoRejectionReason("SALDO_INSUFICIENTE", "Saldo insuficiente"),
                new CelcoinPixAutoRejectionReason("CONSENTIMENTO_INVALIDO", "Consentimento não autorizado"),
                new CelcoinPixAutoRejectionReason("PAGAMENTO_DIVERGENTE_CONSENTIMENTO", "Pagamento divergente"),
                new CelcoinPixAutoRejectionReason("FALHA_INFRAESTRUTURA", "Falha transitória"),
                new CelcoinPixAutoRejectionReason("LIMITE_PERIODO_VALOR_EXCEDIDO", "Limite de período excedido"));
    }

    @Override
    public CelcoinPixAutoCallbackResponse processCallback(CelcoinPixAutoCallbackRequest request) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("code", request.code());
        body.put("state", request.state());
        if (StringUtils.hasText(request.idToken())) body.put("id_token", request.idToken());
        if (request.metadata() != null) body.putAll(request.metadata());
        return httpClient.post("/baas/v1/open/itp/payment-initiation/callback", body,
                CelcoinPixAutoCallbackResponse.class, context(null));
    }

    private void ensureConfigured() {
        if (httpClient == null) throw unspecified();
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private QueryBuilder query() { return new QueryBuilder(); }

    private static String encode(String value) {
        return StringUtils.hasText(value) ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8) : "";
    }

    private static Map<String, Object> metadata(Map<String, Object> metadata) {
        return metadata == null ? Map.of() : metadata;
    }

    private static Object required(Map<String, Object> metadata, String field) {
        Object value = metadata.get(field);
        if (value == null || !StringUtils.hasText(String.valueOf(value))) {
            throw new IllegalArgumentException("Pix Automático metadata must contain " + field);
        }
        return value;
    }

    private static void putAmount(Map<String, Object> body, String name, Object value) {
        if (value instanceof BigDecimal amount) body.put(name, amount.toPlainString());
        else if (value != null) body.put(name, value);
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

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin Pix Automático endpoint path is not configured because the official contract was not provided in this first version");
    }
}
