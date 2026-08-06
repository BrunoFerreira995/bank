package com.brunopedraca.celcoin.acquiring;

import com.brunopedraca.celcoin.acquiring.AcquiringDtos.*;

public interface CelcoinAcquiringOperations {
    CelcoinAcquiringAccreditationStatusResponse getAccreditationStatus(String accountId);

    CelcoinAcquiringCustomerResponse createCustomer(CelcoinAcquiringCustomerRequest request, String idempotencyKey);

    CelcoinAcquiringCustomerListResponse listCustomers(CelcoinAcquiringListRequest request);

    CelcoinAcquiringCustomerResponse updateCustomer(CelcoinAcquiringCustomerRequest request);

    void deleteCustomer(String customerId, String idempotencyKey);

    CelcoinAcquiringCardResponse createCard(CelcoinAcquiringCardRequest request, String idempotencyKey);

    CelcoinAcquiringCardListResponse listCards(CelcoinAcquiringListRequest request);

    CelcoinAcquiringCardResponse deactivateCard(String cardId, String idempotencyKey);

    CelcoinAcquiringChargeResponse createCharge(CelcoinAcquiringChargeRequest request, String idempotencyKey);

    CelcoinAcquiringChargeListResponse listCharges(CelcoinAcquiringListRequest request);

    CelcoinAcquiringChargeResponse updateCharge(CelcoinAcquiringChargeRequest request);

    CelcoinAcquiringChargeResponse retryCharge(String chargeId, String idempotencyKey);

    CelcoinAcquiringChargeResponse refundCharge(String chargeId, String idempotencyKey);

    CelcoinAcquiringChargeResponse cancelCharge(String chargeId, String idempotencyKey);

    CelcoinAcquiringChargeResponse captureCharge(String chargeId, String idempotencyKey);

    CelcoinAcquiringReceivablesReportResponse requestReceivablesReport(
            CelcoinAcquiringReceivablesReportRequest request);

    CelcoinAcquiringReceivablesReportResponse getReceivablesReportStatus(String reportId);

    byte[] downloadReceivablesReport(String reportId);

    CelcoinAcquiringPlanResponse createPlan(CelcoinAcquiringPlanRequest request, String idempotencyKey);

    CelcoinAcquiringPlanListResponse listPlans(CelcoinAcquiringListRequest request);

    CelcoinAcquiringPlanResponse updatePlan(CelcoinAcquiringPlanRequest request);

    void deletePlan(String planId, String idempotencyKey);

    CelcoinAcquiringSubscriptionResponse createSubscription(
            CelcoinAcquiringSubscriptionRequest request, String idempotencyKey);

    CelcoinAcquiringSubscriptionResponse createManualSubscription(
            CelcoinAcquiringSubscriptionRequest request, String idempotencyKey);

    CelcoinAcquiringSubscriptionListResponse listSubscriptions(CelcoinAcquiringListRequest request);

    CelcoinAcquiringChargeResponse addSubscriptionTransaction(
            CelcoinAcquiringSubscriptionTransactionRequest request, String idempotencyKey);

    CelcoinAcquiringSubscriptionResponse updateSubscription(CelcoinAcquiringSubscriptionRequest request);

    CelcoinAcquiringSubscriptionResponse updateSubscriptionPayment(
            CelcoinAcquiringSubscriptionPaymentUpdateRequest request);

    CelcoinAcquiringChargeResponse updateSubscriptionTransaction(
            CelcoinAcquiringSubscriptionTransactionRequest request);

    CelcoinAcquiringChargeResponse retrySubscriptionCharge(String transactionId, String idempotencyKey);

    CelcoinAcquiringChargeResponse captureSubscriptionCharge(String transactionId, String idempotencyKey);

    CelcoinAcquiringChargeResponse refundSubscriptionCharge(String transactionId, String idempotencyKey);

    CelcoinAcquiringSubscriptionResponse cancelSubscription(String subscriptionId, String idempotencyKey);

    CelcoinAcquiringChargeResponse cancelSubscriptionTransaction(String transactionId, String idempotencyKey);

    CelcoinAcquiringChargebackListResponse listChargebacks(CelcoinAcquiringListRequest request);

    CelcoinAcquiringChargebackResponse sendChargebackDefense(CelcoinAcquiringChargebackDefenseRequest request);

    CelcoinAcquiringChargebackResponse withdrawChargebackDispute(String chargebackId, String idempotencyKey);

    CelcoinAcquiringWebhookResponse createChargebackWebhook(CelcoinAcquiringWebhookRequest request);

    CelcoinAcquiringChargebackResponse simulateChargeback(String transactionId, String status);

    CelcoinAcquiringCardTokenResponse tokenizeCard(CelcoinAcquiringCardTokenRequest request);

    CelcoinAcquiringCardTokenResponse tokenizeCard(String accountId, CelcoinAcquiringCardTokenRequest request);

    CelcoinAcquiringFeeListResponse listFees(String accountId);

    CelcoinAcquiringTransactionListResponse listTransactions(CelcoinAcquiringListRequest request);

    CelcoinAcquiringReceivablesStatementResponse getReceivablesStatement(CelcoinAcquiringListRequest request);
}
