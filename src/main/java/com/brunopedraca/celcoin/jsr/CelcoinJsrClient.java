package com.brunopedraca.celcoin.jsr;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.jsr.CelcoinJsrDtos.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import org.springframework.util.StringUtils;

public final class CelcoinJsrClient implements CelcoinJsrOperations {
    private static final String BAAS = "/baas/v1/open/itp";
    private static final String V4 = "/open-keys/itp/api/v2";
    private final CelcoinHttpClient httpClient;

    public CelcoinJsrClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Map<String, Object> createEnrollment(EnrollmentRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("data", value(request.data()));
        body.put("authorizationDevice", request.authorizationDevice());
        if (request.tags() != null) body.put("tags", request.tags());
        return httpClient.post(V4 + "/enrollments/v2/payment-initiation", body, Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> processCallback(CallbackRequest request) {
        ensureConfigured();
        return httpClient.post(BAAS + "/payment-initiation/callback", Map.of(
                "code", request.code(), "state", request.state(), "id_token", request.idToken()),
                Map.class, context(null));
    }

    @Override
    public Map<String, Object> fidoRegistrationOptions(String enrollmentId, FidoOptionsRequest request) {
        ensureConfigured();
        return httpClient.post(BAAS + "/enrollments/payment-initiation/" + encode(enrollmentId)
                        + "/fido-registration-options", Map.of("data", Map.of(
                                "rp", request.rp(), "platform", request.platform())),
                Map.class, context(null));
    }

    @Override
    public Map<String, Object> registerFido(String enrollmentId, FidoRegistrationRequest request,
            String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(BAAS + "/itp/enrollments/payment-initiation/" + encode(enrollmentId)
                        + "/fido-registration", Map.of("data", value(request.data())), Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createPaymentInitiation(PaymentInitiationV4Request request,
            String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("brandId", request.brandId());
        body.put("redirectUrl", request.redirectUrl());
        body.put("enrollment", value(request.enrollment()));
        body.put("data", value(request.data()));
        if (request.directoryCallback() != null) body.put("directoryCallback", request.directoryCallback());
        return httpClient.post(V4 + "/payments/v4/payment-initiation", body, Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> fidoSignOptions(String enrollmentId, FidoSignOptionsRequest request) {
        ensureConfigured();
        return httpClient.post(BAAS + "/payment-initiation/" + encode(enrollmentId)
                        + "/fido-sign-options", Map.of("paymentInitiationId", request.paymentInitiationId(),
                                "data", Map.of("rp", request.rp(), "platform", request.platform())),
                Map.class, context(null));
    }

    @Override
    public Map<String, Object> authorizeFido(String enrollmentId, FidoAuthorizationRequest request,
            String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("data", value(request.data()));
        if (request.processPix() != null) body.put("processPix", request.processPix());
        return httpClient.post(BAAS + "/payment-initiation/" + encode(enrollmentId) + "/authorise",
                body, Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> createPix(String paymentInitiationId, PixV4Request request,
            String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(V4 + "/payments/v4/payment-initiation/" + encode(paymentInitiationId)
                        + "/pix", Map.of("data", value(request.data())), Map.class,
                context(idempotencyKey));
    }

    @Override
    public FidoValidationResult validateFidoBiometry(Map<String, Object> assertion) {
        if (assertion == null) return new FidoValidationResult(false, "assertion is required");
        if (!StringUtils.hasText(text(assertion.get("id"))))
            return new FidoValidationResult(false, "assertion.id is required");
        if (!StringUtils.hasText(text(assertion.get("rawId"))))
            return new FidoValidationResult(false, "assertion.rawId is required");
        if (!StringUtils.hasText(text(assertion.get("type"))))
            return new FidoValidationResult(false, "assertion.type is required");
        Object response = assertion.get("response");
        if (!(response instanceof Map<?, ?> responseMap)
                || !StringUtils.hasText(text(responseMap.get("clientDataJSON"))))
            return new FidoValidationResult(false, "assertion.response.clientDataJSON is required");
        return new FidoValidationResult(true, "shape valid; WebAuthn cryptographic validation is device-side");
    }

    private void ensureConfigured() {
        if (httpClient == null) throw new CelcoinIntegrationException("Celcoin JSR HTTP client is not configured");
    }
    private CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }
    private static Object value(Object value) { return value == null ? Map.of() : value; }
    private static String text(Object value) { return value == null ? null : String.valueOf(value); }
    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
