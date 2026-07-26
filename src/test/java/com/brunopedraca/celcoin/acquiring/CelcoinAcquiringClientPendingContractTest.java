package com.brunopedraca.celcoin.acquiring;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringCardRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringCardTokenRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringChargeRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringChargebackDefenseRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringCustomerRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringListRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringPlanRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringReceivablesReportRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringSubscriptionPaymentUpdateRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringSubscriptionRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringSubscriptionTransactionRequest;
import com.brunopedraca.celcoin.acquiring.AcquiringDtos.CelcoinAcquiringWebhookRequest;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinAcquiringClientPendingContractTest {
    private CelcoinAcquiringClient client;

    @BeforeEach
    void setUp() {
        client = new CelcoinAcquiringClient(null);
    }

    @Test
    void shouldRejectSubAcquiringOperationsUntilOfficialContractIsAdded() {
        List<ThrowingCallable> operations = List.of(
                () -> client.getAccreditationStatus("account-1"),
                () -> client.createCustomer(customerRequest(), "customer-1"),
                () -> client.listCustomers(listRequest()),
                () -> client.updateCustomer(customerRequest()),
                () -> client.deleteCustomer("customer-1", "delete-customer-1"),
                () -> client.createCard(cardRequest(), "card-1"),
                () -> client.listCards(listRequest()),
                () -> client.deactivateCard("card-1", "deactivate-card-1"),
                () -> client.createCharge(chargeRequest(), "charge-1"),
                () -> client.listCharges(listRequest()),
                () -> client.updateCharge(chargeRequest()),
                () -> client.retryCharge("charge-1", "retry-charge-1"),
                () -> client.refundCharge("charge-1", "refund-charge-1"),
                () -> client.cancelCharge("charge-1", "cancel-charge-1"),
                () -> client.captureCharge("charge-1", "capture-charge-1"),
                () -> client.requestReceivablesReport(reportRequest()),
                () -> client.getReceivablesReportStatus("report-1"),
                () -> client.downloadReceivablesReport("report-1"),
                () -> client.createPlan(planRequest(), "plan-1"),
                () -> client.listPlans(listRequest()),
                () -> client.updatePlan(planRequest()),
                () -> client.deletePlan("plan-1", "delete-plan-1"),
                () -> client.createSubscription(subscriptionRequest(), "subscription-1"),
                () -> client.createManualSubscription(subscriptionRequest(), "manual-subscription-1"),
                () -> client.listSubscriptions(listRequest()),
                () -> client.addSubscriptionTransaction(subscriptionTransactionRequest(), "subscription-transaction-1"),
                () -> client.updateSubscription(subscriptionRequest()),
                () -> client.updateSubscriptionPayment(new CelcoinAcquiringSubscriptionPaymentUpdateRequest("subscription-1", "card-1", null)),
                () -> client.updateSubscriptionTransaction(subscriptionTransactionRequest()),
                () -> client.retrySubscriptionCharge("transaction-1", "retry-subscription-charge-1"),
                () -> client.captureSubscriptionCharge("transaction-1", "capture-subscription-charge-1"),
                () -> client.refundSubscriptionCharge("transaction-1", "refund-subscription-charge-1"),
                () -> client.cancelSubscription("subscription-1", "cancel-subscription-1"),
                () -> client.cancelSubscriptionTransaction("transaction-1", "cancel-transaction-1"),
                () -> client.listChargebacks(listRequest()),
                () -> client.sendChargebackDefense(new CelcoinAcquiringChargebackDefenseRequest("chargeback-1", List.of(), "defense")),
                () -> client.withdrawChargebackDispute("chargeback-1", "withdraw-chargeback-1"),
                () -> client.createChargebackWebhook(new CelcoinAcquiringWebhookRequest(
                        "chargeback.created", "https://example.com/webhook", null, null)),
                () -> client.simulateChargeback("transaction-1", "OPEN"),
                () -> client.tokenizeCard(new CelcoinAcquiringCardTokenRequest("Maria Silva", "4111111111111111", "12", "2030", "123")),
                () -> client.listFees("account-1"),
                () -> client.listTransactions(listRequest()),
                () -> client.getReceivablesStatement(listRequest()));

        operations.forEach(this::assertPendingContract);
    }

    private void assertPendingContract(ThrowingCallable callable) {
        assertThatThrownBy(callable::call)
                .isInstanceOf(CelcoinIntegrationException.class)
                .hasMessageContaining("Celcoin acquiring endpoint path is not configured");
    }

    private CelcoinAcquiringCustomerRequest customerRequest() {
        return new CelcoinAcquiringCustomerRequest("customer-1", "12345678901", "Maria Silva", "maria@example.com", null, null, null);
    }

    private CelcoinAcquiringCardRequest cardRequest() {
        return new CelcoinAcquiringCardRequest("customer-1", "Maria Silva", "4111111111111111", "12", "2030", "123", null);
    }

    private CelcoinAcquiringChargeRequest chargeRequest() {
        return new CelcoinAcquiringChargeRequest(
                "charge-1", "customer-1", "card-1", BigDecimal.TEN, "BRL", "charge", false, null);
    }

    private CelcoinAcquiringReceivablesReportRequest reportRequest() {
        return new CelcoinAcquiringReceivablesReportRequest("account-1", LocalDate.now().minusDays(7), LocalDate.now(), null);
    }

    private CelcoinAcquiringPlanRequest planRequest() {
        return new CelcoinAcquiringPlanRequest("plan-1", "Mensal", BigDecimal.TEN, "BRL", "MONTH", 1, null);
    }

    private CelcoinAcquiringSubscriptionRequest subscriptionRequest() {
        return new CelcoinAcquiringSubscriptionRequest(
                "subscription-1", "customer-1", "plan-1", "card-1", BigDecimal.TEN, "MONTH", LocalDate.now(), null);
    }

    private CelcoinAcquiringSubscriptionTransactionRequest subscriptionTransactionRequest() {
        return new CelcoinAcquiringSubscriptionTransactionRequest("transaction-1", "subscription-1", BigDecimal.TEN, LocalDate.now(), null);
    }

    private CelcoinAcquiringListRequest listRequest() {
        return new CelcoinAcquiringListRequest("account-1", "customer-1", "ACTIVE", 0, 20);
    }

    @FunctionalInterface
    private interface ThrowingCallable {
        void call();
    }
}
