package com.brunopedraca.celcoin.cards;

import com.brunopedraca.celcoin.cards.CardDtos.*;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StringUtils;

public class CelcoinCardClient implements CelcoinCardOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinCardClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinCardAccountResponse createCardAccount(CelcoinCardAccountRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/cards/v1/accounts", request, CelcoinCardAccountResponse.class, context(idempotencyKey));
    }

    public CelcoinCardAccountResponse getCardAccount(String cardAccountId) {
        ensureConfigured();
        return httpClient.get("/cards/v1/accounts/" + encode(cardAccountId), CelcoinCardAccountResponse.class, context(null));
    }

    public CelcoinCardLimitResponse getCardAccountLimits(String cardAccountId) {
        ensureConfigured();
        return httpClient.get("/cards/v1/accounts/" + encode(cardAccountId) + "/limits",
                CelcoinCardLimitResponse.class, context(null));
    }

    public CelcoinCardAccountResponse updateCustomer(CelcoinCardCustomerUpdateRequest request) {
        ensureConfigured();
        return httpClient.put("/cards/v1/accounts/" + encode(request.cardAccountId()), request,
                CelcoinCardAccountResponse.class, context(null));
    }

    public CelcoinCardAccountResponse updatePhone(CelcoinCardPhoneUpdateRequest request) {
        ensureConfigured();
        return httpClient.put("/cards/v1/accounts/" + encode(request.cardAccountId()) + "/phone", request,
                CelcoinCardAccountResponse.class, context(null));
    }

    public CelcoinCardAccountResponse cancelAccountAndCards(
            String cardAccountId, String reason, String idempotencyKey) {
        ensureConfigured();
        return httpClient.delete("/cards/v1/accounts/" + encode(cardAccountId) + "?reason=" + encode(reason),
                null, CelcoinCardAccountResponse.class, context(idempotencyKey));
    }

    public CelcoinCardAccountResponse updateAddress(CelcoinCardAddressRequest request) {
        ensureConfigured();
        return httpClient.put("/cards/v1/accounts/" + encode(request.cardAccountId()) + "/address", request,
                CelcoinCardAccountResponse.class, context(null));
    }

    public CelcoinCardResponse issueCard(CelcoinCardIssueRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/cards/v1/accounts/" + encode(request.cardAccountId()) + "/cards", request,
                CelcoinCardResponse.class, context(idempotencyKey));
    }

    public CelcoinCardResponse reissueCard(CelcoinCardIssueRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/cards/v1/accounts/" + encode(request.cardAccountId()) + "/cards/reissue", request,
                CelcoinCardResponse.class, context(idempotencyKey));
    }

    public CelcoinCardTrackingResponse getTracking(String cardId) {
        ensureConfigured();
        return httpClient.get("/cards/v1/cards/" + encode(cardId) + "/tracking",
                CelcoinCardTrackingResponse.class, context(null));
    }

    public CelcoinCardTrackingResponse simulateTracking(String cardId, String status) {
        ensureConfigured();
        return httpClient.post("/cards/v1/cards/" + encode(cardId) + "/tracking/simulate",
                java.util.Map.of("status", status), CelcoinCardTrackingResponse.class, context(null));
    }

    public CelcoinCardResponse activateCard(String cardId, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/cards/v1/cards/" + encode(cardId) + "/activate", null,
                CelcoinCardResponse.class, context(idempotencyKey));
    }

    public CelcoinCardResponse updateStatus(CelcoinCardStatusUpdateRequest request) {
        ensureConfigured();
        return httpClient.put("/cards/v1/cards/" + encode(request.cardId()) + "/status", request,
                CelcoinCardResponse.class, context(null));
    }

    public CelcoinCardSensitiveDataResponse getSensitiveData(String cardId) {
        ensureConfigured();
        return httpClient.get("/cards/v1/cards/" + encode(cardId) + "/sensitive-data",
                CelcoinCardSensitiveDataResponse.class, context(null));
    }

    public CelcoinCardListResponse listCards(CelcoinCardListRequest request) {
        ensureConfigured();
        String path = "/cards/v1/cards?" + query().param("accountId", request.cardAccountId())
                .param("status", request.status()).param("type", request.type())
                .param("page", request.page()).param("size", request.size());
        return httpClient.get(path, CelcoinCardListResponse.class, context(null));
    }

    public CelcoinCardResponse updatePin(CelcoinCardPinUpdateRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.put("/cards/v1/cards/" + encode(request.cardId()) + "/pin", request,
                CelcoinCardResponse.class, context(idempotencyKey));
    }

    public CelcoinCardTransactionSimulationResponse simulateTransaction(
            CelcoinCardTransactionSimulationRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/cards/v1/cards/" + encode(request.cardId()) + "/transactions/simulate", request,
                CelcoinCardTransactionSimulationResponse.class, context(idempotencyKey));
    }

    public CelcoinCardWebhookResponse createWebhook(CelcoinCardWebhookRequest request) {
        ensureConfigured();
        return httpClient.post("/cards/v1/webhooks", request, CelcoinCardWebhookResponse.class, context(null));
    }

    public CelcoinCardWebhookResponse updateWebhook(CelcoinCardWebhookRequest request) {
        ensureConfigured();
        return httpClient.put("/cards/v1/webhooks/" + encode(request.eventType()), request,
                CelcoinCardWebhookResponse.class, context(null));
    }

    public CelcoinCardWebhookTemplateResponse getWebhookTemplate(String eventType) {
        ensureConfigured();
        return httpClient.get("/cards/v1/webhooks/templates/" + encode(eventType),
                CelcoinCardWebhookTemplateResponse.class, context(null));
    }

    public CelcoinCardWebhookResponse resendPendingWebhook(
            CelcoinCardWebhookResendRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/cards/v1/webhooks/resend", request, CelcoinCardWebhookResponse.class,
                context(idempotencyKey));
    }

    public CelcoinCardInvoiceResponse getPostpaidInvoice(String cardAccountId, String invoiceId) {
        ensureConfigured();
        return httpClient.get("/cards/v1/accounts/" + encode(cardAccountId) + "/invoices/" + encode(invoiceId),
                CelcoinCardInvoiceResponse.class, context(null));
    }

    private void ensureConfigured() {
        if (httpClient == null) {
            throw unspecified();
        }
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private static String encode(String value) {
        return StringUtils.hasText(value) ? URLEncoder.encode(value, StandardCharsets.UTF_8) : "";
    }

    private QueryBuilder query() { return new QueryBuilder(); }

    private static final class QueryBuilder {
        private final StringBuilder value = new StringBuilder();
        QueryBuilder param(String name, Object item) {
            if (item != null && StringUtils.hasText(String.valueOf(item))) {
                if (!value.isEmpty()) value.append('&');
                value.append(name).append('=').append(encode(String.valueOf(item)));
            }
            return this;
        }
        @Override public String toString() { return value.toString(); }
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin card endpoint path is not configured because the official contract was not provided in this first version");
    }
}
