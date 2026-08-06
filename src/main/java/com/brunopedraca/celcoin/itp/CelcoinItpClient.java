package com.brunopedraca.celcoin.itp;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.itp.CelcoinItpDtos.*;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class CelcoinItpClient implements CelcoinItpOperations {
    private static final String BASE = "/baas/v1/open/itp";
    private final CelcoinHttpClient httpClient;

    public CelcoinItpClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Map<String, Object> createConsent(ConsentRequest request, String idempotencyKey) {
        ensureConfigured();
        if (request.amount().signum() <= 0 || request.amount().scale() > 2) {
            throw new IllegalArgumentException("amount must be positive with at most two decimal places");
        }
        if (request.date().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("payment date must be today or in the future");
        }
        if ("DICT".equals(request.localInstrument()) && !StringUtils.hasText(request.proxy())) {
            throw new IllegalArgumentException("proxy is required when localInstrument is DICT");
        }
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("localInstrument", request.localInstrument());
        paymentDetails.put("creditorAccount", request.creditorAccount());
        if (StringUtils.hasText(request.proxy())) paymentDetails.put("proxy", request.proxy());
        Map<String, Object> payment = Map.of(
                "type", "PIX",
                "amount", request.amount().setScale(2, RoundingMode.UNNECESSARY).toPlainString(),
                "currency", "BRL",
                "date", request.date().toString(),
                "details", paymentDetails);
        Map<String, Object> data = new HashMap<>();
        data.put("loggedUser", Map.of("document", Map.of(
                "identification", request.loggedUserDocument(), "rel", "CPF")));
        data.put("creditor", Map.of("cpfCnpj", request.creditorCpfCnpj(),
                "personType", request.creditorPersonType(), "name", request.creditorName()));
        data.put("payment", payment);
        if (request.debtorAccount() != null) data.put("debtorAccount", request.debtorAccount());
        return httpClient.post(BASE + "/payment-initiation", Map.of(
                        "brandId", request.brandId(), "redirectUrl", request.redirectUrl(), "data", data),
                Map.class, context(idempotencyKey));
    }

    @Override
    public Map<String, Object> processCallback(CallbackRequest request) {
        ensureConfigured();
        return httpClient.post(BASE + "/payment-initiation/callback", Map.of(
                "code", request.code(), "id_token", request.idToken(), "state", request.state()),
                Map.class, context(null));
    }

    @Override
    public Map<String, Object> getPaymentInitiation(String paymentInitiationId) {
        ensureConfigured();
        return httpClient.get(BASE + "/payment-initiation/" + encode(paymentInitiationId),
                Map.class, context(null));
    }

    @Override
    public Map<String, Object> createPix(String paymentInitiationId, PixRequest request,
            String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BASE + "/payment-initiation/" + encode(paymentInitiationId) + "/pix",
                request == null || request.data() == null ? Map.of() : Map.of("data", request.data()),
                Map.class, context(idempotencyKey));
    }

    @Override
    public WebhookEvent parseWebhook(Map<String, Object> payload) {
        if (payload == null) throw new IllegalArgumentException("webhook payload is required");
        Map<String, Object> data = map(payload.get("data"));
        return new WebhookEvent(text(payload.get("event")), text(payload.get("timestamp")),
                new WebhookData(text(data.get("paymentInitiationId")), text(data.get("paymentId")),
                        text(data.get("endToEndId")), text(data.get("previousStatus")),
                        text(data.get("currentStatus")), text(data.get("rejectionReason")), data), payload);
    }

    @Override public ItpStateMachine.ConsentState consentState(String status) {
        return ItpStateMachine.consent(status);
    }
    @Override public ItpStateMachine.PaymentState paymentState(String status) {
        return ItpStateMachine.payment(status);
    }
    @Override public ErrorDescriptor paymentError(String code) {
        return ItpPaymentErrors.describe(code);
    }

    private void ensureConfigured() {
        if (httpClient == null) throw new CelcoinIntegrationException("Celcoin ITP HTTP client is not configured");
    }
    private CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }
    private static String text(Object value) { return value == null ? null : String.valueOf(value); }
    @SuppressWarnings("unchecked") private static Map<String, Object> map(Object value) {
        return value instanceof Map<?, ?> ? (Map<String, Object>) value : Map.of();
    }
    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
