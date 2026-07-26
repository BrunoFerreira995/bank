package com.brunopedraca.celcoin.cards;

import com.brunopedraca.celcoin.cards.CardDtos.*;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;

public class CelcoinCardClient implements CelcoinCardOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinCardClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinCardAccountResponse createCardAccount(CelcoinCardAccountRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardAccountResponse getCardAccount(String cardAccountId) {
        throw unspecified();
    }

    public CelcoinCardLimitResponse getCardAccountLimits(String cardAccountId) {
        throw unspecified();
    }

    public CelcoinCardAccountResponse updateCustomer(CelcoinCardCustomerUpdateRequest request) {
        throw unspecified();
    }

    public CelcoinCardAccountResponse updatePhone(CelcoinCardPhoneUpdateRequest request) {
        throw unspecified();
    }

    public CelcoinCardAccountResponse cancelAccountAndCards(String cardAccountId, String reason, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardAccountResponse updateAddress(CelcoinCardAddressRequest request) {
        throw unspecified();
    }

    public CelcoinCardResponse issueCard(CelcoinCardIssueRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardResponse reissueCard(CelcoinCardIssueRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardTrackingResponse getTracking(String cardId) {
        throw unspecified();
    }

    public CelcoinCardTrackingResponse simulateTracking(String cardId, String status) {
        throw unspecified();
    }

    public CelcoinCardResponse activateCard(String cardId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardResponse updateStatus(CelcoinCardStatusUpdateRequest request) {
        throw unspecified();
    }

    public CelcoinCardSensitiveDataResponse getSensitiveData(String cardId) {
        throw unspecified();
    }

    public CelcoinCardListResponse listCards(CelcoinCardListRequest request) {
        throw unspecified();
    }

    public CelcoinCardResponse updatePin(CelcoinCardPinUpdateRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardTransactionSimulationResponse simulateTransaction(
            CelcoinCardTransactionSimulationRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardWebhookResponse createWebhook(CelcoinCardWebhookRequest request) {
        throw unspecified();
    }

    public CelcoinCardWebhookResponse updateWebhook(CelcoinCardWebhookRequest request) {
        throw unspecified();
    }

    public CelcoinCardWebhookTemplateResponse getWebhookTemplate(String eventType) {
        throw unspecified();
    }

    public CelcoinCardWebhookResponse resendPendingWebhook(CelcoinCardWebhookResendRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinCardInvoiceResponse getPostpaidInvoice(String cardAccountId, String invoiceId) {
        throw unspecified();
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin card endpoint path is not configured because the official contract was not provided in this first version");
    }
}
