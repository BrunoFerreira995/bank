package com.brunopedraca.celcoin.boleto;

import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoListResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPeriodRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoAuthorizationRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoAuthorizationResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPaymentRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPaymentResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoIssueRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoIssueResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoApiEnvelope;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import org.springframework.util.StringUtils;

public class CelcoinBoletoClient implements CelcoinBoletoOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinBoletoClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinBoletoResponse issue(CelcoinBoletoRequest request, String idempotencyKey) {
        Map<String, Object> debtor = request.payer() == null ? new HashMap<>() : new HashMap<>(request.payer());
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("account", request.accountId());
        CelcoinBoletoIssueRequest issue = new CelcoinBoletoIssueRequest(
                java.util.UUID.randomUUID().toString(), null, 0,
                request.dueDate() == null ? LocalDate.now().plusDays(1).toString() : request.dueDate().toString(),
                request.amount(), null, null, debtor, receiver, null, null, null, null, "BOLEPIX");
        return issue(issue, idempotencyKey);
    }

    @Override
    public CelcoinBoletoResponse issue(CelcoinBoletoIssueRequest request, String idempotencyKey) {
        ensureConfigured();
        CelcoinBoletoIssueResponse response = httpClient.post(
                "/baas/v2/charge", request, CelcoinBoletoIssueResponse.class, context(idempotencyKey));
        String transactionId = response == null || response.body() == null ? null : response.body().transactionId();
        return new CelcoinBoletoResponse(transactionId, response == null ? null : response.status(), null, Map.of());
    }

    public CelcoinBoletoResponse get(String boletoId) {
        ensureConfigured();
        return toResponse(httpClient.get("/baas/v2/charge?TransactionId=" + encode(boletoId),
                CelcoinBoletoApiEnvelope.class, context(null)));
    }

    @Override
    public CelcoinBoletoAuthorizationResponse authorize(CelcoinBoletoAuthorizationRequest request) {
        ensureConfigured();
        return httpClient.post(
                "/v5/transactions/billpayments/authorize",
                request,
                CelcoinBoletoAuthorizationResponse.class,
                context(null));
    }

    @Override
    public CelcoinBoletoPaymentResponse pay(CelcoinBoletoPaymentRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post(
                "/baas/v2/billpayment", request, CelcoinBoletoPaymentResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinBoletoPaymentResponse getPaymentStatus(String paymentId, String clientRequestId) {
        ensureConfigured();
        StringBuilder path = new StringBuilder("/baas/v2/billpayment/status?");
        append(path, "ClientRequestId", clientRequestId);
        append(path, "Id", paymentId);
        return httpClient.get(path.toString(), CelcoinBoletoPaymentResponse.class, context(null));
    }

    public CelcoinBoletoListResponse list(CelcoinBoletoPeriodRequest request) {
        ensureConfigured();
        StringBuilder path = new StringBuilder("/baas/v2/charge/search?");
        append(path, "Sort", request.sort());
        append(path, "Page", number(request.page()));
        append(path, "Limit", number(request.limit()));
        append(path, "Status", request.status());
        append(path, "ReceiverDocument", request.receiverDocument());
        append(path, "ReceiverAccount", request.receiverAccount());
        append(path, "DebtorDocument", request.debtorDocument());
        append(path, "DateType", request.dateType());
        append(path, "Start", request.startDate() == null ? null : request.startDate().toString());
        append(path, "End", request.endDate() == null ? null : request.endDate().toString());
        CelcoinBoletoApiEnvelope response = httpClient.get(path.toString(), CelcoinBoletoApiEnvelope.class, context(null));
        List<CelcoinBoletoResponse> boletos = new ArrayList<>();
        if (response != null && response.body() != null) {
            Object items = response.body().get("items");
            if (!(items instanceof List<?>)) items = response.body().get("charges");
            if (items instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map<?, ?> map) {
                        boletos.add(new CelcoinBoletoResponse(String.valueOf(map.get("transactionId")),
                                String.valueOf(map.get("status")), String.valueOf(map.get("bankLine")),
                                new HashMap<>((Map<String, Object>) map)));
                    }
                }
            }
        }
        return new CelcoinBoletoListResponse(boletos);
    }

    public void cancel(String boletoId, String idempotencyKey) {
        ensureConfigured();
        httpClient.delete("/baas/v2/charge/" + encode(boletoId),
                Map.of("reason", "Cancelamento solicitado pelo cliente"),
                CelcoinBoletoApiEnvelope.class, context(idempotencyKey));
    }

    public byte[] downloadPdf(String boletoId) {
        ensureConfigured();
        return httpClient.download("/baas/v2/charge/pdf/" + encode(boletoId), context(null));
    }

    private CelcoinBoletoResponse toResponse(CelcoinBoletoApiEnvelope envelope) {
        if (envelope == null || envelope.body() == null) {
            return new CelcoinBoletoResponse(null, envelope == null ? null : envelope.status(), null, Map.of());
        }
        Map<String, Object> body = envelope.body();
        Object boleto = body.get("boleto");
        String line = boleto instanceof Map<?, ?> map ? String.valueOf(map.get("bankLine")) : null;
        return new CelcoinBoletoResponse(String.valueOf(body.get("transactionId")),
                String.valueOf(body.get("status")), line, body);
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin boleto endpoint path is not configured because the official contract was not provided in this first version");
    }

    private void ensureConfigured() {
        if (httpClient == null) {
            throw unspecified();
        }
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private static void append(StringBuilder path, String name, String value) {
        if (StringUtils.hasText(value)) {
            if (path.charAt(path.length() - 1) != '?') {
                path.append('&');
            }
            path.append(name).append('=').append(java.net.URLEncoder.encode(
                    value, java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    private static String number(Integer value) { return value == null ? null : String.valueOf(value); }

    private static String encode(String value) {
        return StringUtils.hasText(value)
                ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8) : "";
    }
}
