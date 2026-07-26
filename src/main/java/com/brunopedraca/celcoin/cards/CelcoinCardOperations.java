package com.brunopedraca.celcoin.cards;

import com.brunopedraca.celcoin.cards.CardDtos.*;

public interface CelcoinCardOperations {
    CelcoinCardAccountResponse createCardAccount(CelcoinCardAccountRequest request, String idempotencyKey);

    CelcoinCardAccountResponse getCardAccount(String cardAccountId);

    CelcoinCardLimitResponse getCardAccountLimits(String cardAccountId);

    CelcoinCardAccountResponse updateCustomer(CelcoinCardCustomerUpdateRequest request);

    CelcoinCardAccountResponse updatePhone(CelcoinCardPhoneUpdateRequest request);

    CelcoinCardAccountResponse cancelAccountAndCards(String cardAccountId, String reason, String idempotencyKey);

    CelcoinCardAccountResponse updateAddress(CelcoinCardAddressRequest request);

    CelcoinCardResponse issueCard(CelcoinCardIssueRequest request, String idempotencyKey);

    CelcoinCardResponse reissueCard(CelcoinCardIssueRequest request, String idempotencyKey);

    CelcoinCardTrackingResponse getTracking(String cardId);

    CelcoinCardTrackingResponse simulateTracking(String cardId, String status);

    CelcoinCardResponse activateCard(String cardId, String idempotencyKey);

    CelcoinCardResponse updateStatus(CelcoinCardStatusUpdateRequest request);

    CelcoinCardSensitiveDataResponse getSensitiveData(String cardId);

    CelcoinCardListResponse listCards(CelcoinCardListRequest request);

    CelcoinCardResponse updatePin(CelcoinCardPinUpdateRequest request, String idempotencyKey);

    CelcoinCardTransactionSimulationResponse simulateTransaction(
            CelcoinCardTransactionSimulationRequest request, String idempotencyKey);

    CelcoinCardWebhookResponse createWebhook(CelcoinCardWebhookRequest request);

    CelcoinCardWebhookResponse updateWebhook(CelcoinCardWebhookRequest request);

    CelcoinCardWebhookTemplateResponse getWebhookTemplate(String eventType);

    CelcoinCardWebhookResponse resendPendingWebhook(CelcoinCardWebhookResendRequest request, String idempotencyKey);

    CelcoinCardInvoiceResponse getPostpaidInvoice(String cardAccountId, String invoiceId);
}
