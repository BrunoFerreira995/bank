package com.brunopedraca.celcoin.acquiring;

import com.brunopedraca.celcoin.acquiring.AcquiringDtos.*;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import org.springframework.util.StringUtils;

public class CelcoinAcquiringClient implements CelcoinAcquiringOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinAcquiringClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CelcoinAcquiringAccreditationStatusResponse getAccreditationStatus(String accountId) {
        ensureConfigured();
        return httpClient.get("/baas/v1/cash/accreditation?account=" + encode(accountId),
                CelcoinAcquiringAccreditationStatusResponse.class, context(null));
    }

    @Override
    public CelcoinAcquiringCustomerResponse createCustomer(CelcoinAcquiringCustomerRequest request, String key) {
        ensureConfigured();
        return httpClient.post("/baas/v1/cash/customers", request,
                CelcoinAcquiringCustomerResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringCustomerListResponse listCustomers(CelcoinAcquiringListRequest request) {
        ensureConfigured();
        return httpClient.get("/baas/v1/cash/customers?" + listQuery(request),
                CelcoinAcquiringCustomerListResponse.class, context(null));
    }

    @Override
    public CelcoinAcquiringCustomerResponse updateCustomer(CelcoinAcquiringCustomerRequest request) {
        ensureConfigured();
        return httpClient.put("/baas/v1/cash/customers/" + encode(request.customerId()) + "/myId",
                request, CelcoinAcquiringCustomerResponse.class, context(null));
    }

    @Override
    public void deleteCustomer(String customerId, String key) {
        ensureConfigured();
        httpClient.delete("/baas/v1/cash/customers/" + encode(customerId) + "/myId", Map.of(),
                Map.class, context(key));
    }

    @Override
    public CelcoinAcquiringCardResponse createCard(CelcoinAcquiringCardRequest request, String key) {
        ensureConfigured();
        String path = "/baas/v1/cash/cards/" + encode(request.customerId()) + "/myId?account="
                + encode(account(request.metadata()));
        Map<String, Object> body = new HashMap<>();
        body.put("number", request.number());
        body.put("holder", request.holderName());
        body.put("expiresAt", request.expirationYear() + "-" + request.expirationMonth());
        if (request.securityCode() != null) body.put("cvv", request.securityCode());
        return httpClient.post(path, body, CelcoinAcquiringCardResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringCardListResponse listCards(CelcoinAcquiringListRequest request) {
        ensureConfigured();
        return httpClient.get("/baas/v1/cash/cards?" + listQuery(request),
                CelcoinAcquiringCardListResponse.class, context(null));
    }

    @Override
    public CelcoinAcquiringCardResponse deactivateCard(String cardId, String key) {
        ensureConfigured();
        return httpClient.delete("/baas/v1/cash/cards/" + encode(cardId) + "/myId", Map.of(),
                CelcoinAcquiringCardResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringChargeResponse createCharge(CelcoinAcquiringChargeRequest request, String key) {
        ensureConfigured();
        String path = "/baas/v1/cash/charges?account=" + encode(account(request.metadata()));
        return httpClient.post(path, request, CelcoinAcquiringChargeResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringChargeListResponse listCharges(CelcoinAcquiringListRequest request) {
        ensureConfigured();
        return httpClient.get("/baas/v1/cash/charges?" + listQuery(request),
                CelcoinAcquiringChargeListResponse.class, context(null));
    }

    @Override
    public CelcoinAcquiringChargeResponse updateCharge(CelcoinAcquiringChargeRequest request) {
        ensureConfigured();
        return httpClient.put(chargePath(request.chargeId()) + "/myId", request,
                CelcoinAcquiringChargeResponse.class, context(null));
    }

    @Override
    public CelcoinAcquiringChargeResponse retryCharge(String chargeId, String key) {
        ensureConfigured();
        return httpClient.post(chargePath(chargeId) + "/myId/retry", null,
                CelcoinAcquiringChargeResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringChargeResponse refundCharge(String chargeId, String key) {
        ensureConfigured();
        return httpClient.put(chargePath(chargeId) + "/myId/reverse", Map.of(),
                CelcoinAcquiringChargeResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringChargeResponse cancelCharge(String chargeId, String key) {
        ensureConfigured();
        return httpClient.delete(chargePath(chargeId) + "/myId", Map.of(),
                CelcoinAcquiringChargeResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringChargeResponse captureCharge(String chargeId, String key) {
        ensureConfigured();
        return httpClient.put(chargePath(chargeId) + "/myId/capture", Map.of(),
                CelcoinAcquiringChargeResponse.class, context(key));
    }

    @Override
    public CelcoinAcquiringReceivablesReportResponse requestReceivablesReport(
            CelcoinAcquiringReceivablesReportRequest request) {
        ensureConfigured();
        return httpClient.put("/baas/v1/cash/receivables", request,
                CelcoinAcquiringReceivablesReportResponse.class, context(null));
    }

    @Override
    public CelcoinAcquiringReceivablesReportResponse getReceivablesReportStatus(String reportId) {
        ensureConfigured();
        return httpClient.get("/baas/v1/cash/receivables/" + encode(reportId),
                CelcoinAcquiringReceivablesReportResponse.class, context(null));
    }

    @Override
    public byte[] downloadReceivablesReport(String reportId) {
        ensureConfigured();
        return httpClient.download("/baas/v1/cash/receivables/" + encode(reportId) + "/download", context(null));
    }

    @Override
    public CelcoinAcquiringPlanResponse createPlan(CelcoinAcquiringPlanRequest request, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringPlanListResponse listPlans(CelcoinAcquiringListRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringPlanResponse updatePlan(CelcoinAcquiringPlanRequest request) {
        return pending();
    }

    @Override
    public void deletePlan(String planId, String key) {
        pendingVoid();
    }

    @Override
    public CelcoinAcquiringSubscriptionResponse createSubscription(
            CelcoinAcquiringSubscriptionRequest request, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringSubscriptionResponse createManualSubscription(
            CelcoinAcquiringSubscriptionRequest request, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringSubscriptionListResponse listSubscriptions(CelcoinAcquiringListRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargeResponse addSubscriptionTransaction(
            CelcoinAcquiringSubscriptionTransactionRequest request, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringSubscriptionResponse updateSubscription(CelcoinAcquiringSubscriptionRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringSubscriptionResponse updateSubscriptionPayment(
            CelcoinAcquiringSubscriptionPaymentUpdateRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargeResponse updateSubscriptionTransaction(
            CelcoinAcquiringSubscriptionTransactionRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargeResponse retrySubscriptionCharge(String transactionId, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargeResponse captureSubscriptionCharge(String transactionId, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargeResponse refundSubscriptionCharge(String transactionId, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringSubscriptionResponse cancelSubscription(String subscriptionId, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargeResponse cancelSubscriptionTransaction(String transactionId, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargebackListResponse listChargebacks(CelcoinAcquiringListRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargebackResponse sendChargebackDefense(CelcoinAcquiringChargebackDefenseRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargebackResponse withdrawChargebackDispute(String chargebackId, String key) {
        return pending();
    }

    @Override
    public CelcoinAcquiringWebhookResponse createChargebackWebhook(CelcoinAcquiringWebhookRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringChargebackResponse simulateChargeback(String transactionId, String status) {
        return pending();
    }

    @Override
    public CelcoinAcquiringCardTokenResponse tokenizeCard(CelcoinAcquiringCardTokenRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringFeeListResponse listFees(String accountId) {
        return pending();
    }

    @Override
    public CelcoinAcquiringTransactionListResponse listTransactions(CelcoinAcquiringListRequest request) {
        return pending();
    }

    @Override
    public CelcoinAcquiringReceivablesStatementResponse getReceivablesStatement(
            CelcoinAcquiringListRequest request) {
        return pending();
    }

    private String chargePath(String chargeId) {
        return "/baas/v1/cash/charges/" + encode(chargeId);
    }

    private String listQuery(CelcoinAcquiringListRequest request) {
        StringBuilder q = new StringBuilder();
        param(q, "account", request.accountId());
        param(q, "customerId", request.customerId());
        param(q, "status", request.status());
        param(q, "page", request.page());
        param(q, "limit", request.size());
        return q.toString();
    }

    private static String account(Map<String, Object> metadata) {
        Object account = metadata == null ? null : metadata.get("account");
        return account == null ? "" : String.valueOf(account);
    }

    private static void param(StringBuilder q, String name, Object value) {
        if (value != null && StringUtils.hasText(String.valueOf(value))) {
            if (!q.isEmpty()) q.append('&');
            q.append(name).append('=').append(encode(String.valueOf(value)));
        }
    }

    private static String encode(String value) {
        return StringUtils.hasText(value) ? URLEncoder.encode(value, StandardCharsets.UTF_8) : "";
    }

    private CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }

    private void ensureConfigured() {
        if (httpClient == null) throw pendingException();
    }

    private <T> T pending() {
        throw pendingException();
    }

    private void pendingVoid() {
        throw pendingException();
    }

    private CelcoinIntegrationException pendingException() {
        return new CelcoinIntegrationException(
                "Celcoin acquiring endpoint path is not configured because the official contract was not provided");
    }
}
