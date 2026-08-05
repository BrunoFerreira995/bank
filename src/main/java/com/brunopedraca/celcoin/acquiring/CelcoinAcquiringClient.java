package com.brunopedraca.celcoin.acquiring;

import com.brunopedraca.celcoin.acquiring.AcquiringDtos.*;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;

public class CelcoinAcquiringClient implements CelcoinAcquiringOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinAcquiringClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinAcquiringAccreditationStatusResponse getAccreditationStatus(String accountId) {
        throw unspecified();
    }

    public CelcoinAcquiringCustomerResponse createCustomer(
            CelcoinAcquiringCustomerRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringCustomerListResponse listCustomers(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringCustomerResponse updateCustomer(CelcoinAcquiringCustomerRequest request) {
        throw unspecified();
    }

    public void deleteCustomer(String customerId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringCardResponse createCard(CelcoinAcquiringCardRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringCardListResponse listCards(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringCardResponse deactivateCard(String cardId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse createCharge(CelcoinAcquiringChargeRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeListResponse listCharges(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse updateCharge(CelcoinAcquiringChargeRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse retryCharge(String chargeId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse refundCharge(String chargeId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse cancelCharge(String chargeId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse captureCharge(String chargeId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringReceivablesReportResponse requestReceivablesReport(
            CelcoinAcquiringReceivablesReportRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringReceivablesReportResponse getReceivablesReportStatus(String reportId) {
        throw unspecified();
    }

    public byte[] downloadReceivablesReport(String reportId) {
        throw unspecified();
    }

    public CelcoinAcquiringPlanResponse createPlan(CelcoinAcquiringPlanRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringPlanListResponse listPlans(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringPlanResponse updatePlan(CelcoinAcquiringPlanRequest request) {
        throw unspecified();
    }

    public void deletePlan(String planId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringSubscriptionResponse createSubscription(
            CelcoinAcquiringSubscriptionRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringSubscriptionResponse createManualSubscription(
            CelcoinAcquiringSubscriptionRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringSubscriptionListResponse listSubscriptions(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse addSubscriptionTransaction(
            CelcoinAcquiringSubscriptionTransactionRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringSubscriptionResponse updateSubscription(CelcoinAcquiringSubscriptionRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringSubscriptionResponse updateSubscriptionPayment(
            CelcoinAcquiringSubscriptionPaymentUpdateRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse updateSubscriptionTransaction(
            CelcoinAcquiringSubscriptionTransactionRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse retrySubscriptionCharge(String transactionId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse captureSubscriptionCharge(String transactionId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse refundSubscriptionCharge(String transactionId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringSubscriptionResponse cancelSubscription(String subscriptionId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargeResponse cancelSubscriptionTransaction(String transactionId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringChargebackListResponse listChargebacks(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargebackResponse sendChargebackDefense(CelcoinAcquiringChargebackDefenseRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargebackResponse withdrawChargebackDispute(String chargebackId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAcquiringWebhookResponse createChargebackWebhook(CelcoinAcquiringWebhookRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringChargebackResponse simulateChargeback(String transactionId, String status) {
        throw unspecified();
    }

    public CelcoinAcquiringCardTokenResponse tokenizeCard(CelcoinAcquiringCardTokenRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringFeeListResponse listFees(String accountId) {
        throw unspecified();
    }

    public CelcoinAcquiringTransactionListResponse listTransactions(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    public CelcoinAcquiringReceivablesStatementResponse getReceivablesStatement(CelcoinAcquiringListRequest request) {
        throw unspecified();
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin acquiring endpoint path is not configured because the official contract was not provided in this first version");
    }
}
