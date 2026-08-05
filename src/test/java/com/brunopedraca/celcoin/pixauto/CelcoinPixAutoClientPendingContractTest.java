package com.brunopedraca.celcoin.pixauto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.pixauto.PixAutoDtos.CelcoinPixAutoConsentRequest;
import com.brunopedraca.celcoin.pixauto.PixAutoDtos.CelcoinPixAutoListRequest;
import com.brunopedraca.celcoin.pixauto.PixAutoDtos.CelcoinPixAutoReceiveScheduleRequest;
import com.brunopedraca.celcoin.pixauto.PixAutoDtos.CelcoinPixAutoRetryRequest;
import com.brunopedraca.celcoin.pixauto.PixAutoDtos.CelcoinPixAutoScheduleRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinPixAutoClientPendingContractTest {
    private CelcoinPixAutoClient client;

    @BeforeEach
    void setUp() {
        client = new CelcoinPixAutoClient(null);
    }

    @Test
    void shouldRejectConsentUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createConsent(
                new CelcoinPixAutoConsentRequest(
                        "account-1", "12345678901", "Maria Silva", "12345678", BigDecimal.TEN, "MONTHLY", 10, null),
                "consent-1"));
    }

    @Test
    void shouldRejectConsentStatusUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.getConsentStatus("consent-1"));
    }

    @Test
    void shouldRejectConsentListingUntilOfficialContractIsAdded() {
        assertPendingContract(
                () -> client.listConsents(new CelcoinPixAutoListRequest("account-1", "ACTIVE", null, null, 0, 20)));
    }

    @Test
    void shouldRejectConsentCancellationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.cancelConsent("consent-1", "cancel-1"));
    }

    @Test
    void shouldRejectScheduleUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.schedule(
                new CelcoinPixAutoScheduleRequest(
                        "account-1",
                        "consent-1",
                        BigDecimal.TEN,
                        LocalDate.now().plusDays(7),
                        "MONTHLY",
                        10,
                        null),
                "schedule-1"));
    }

    @Test
    void shouldRejectScheduleStatusUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.getScheduleStatus("schedule-1"));
    }

    @Test
    void shouldRejectScheduleListingUntilOfficialContractIsAdded() {
        assertPendingContract(
                () -> client.listSchedules(new CelcoinPixAutoListRequest("account-1", "SCHEDULED", null, null, 0, 20)));
    }

    @Test
    void shouldRejectScheduleCancellationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.cancelSchedule("schedule-1", "cancel-2"));
    }

    @Test
    void shouldRejectLiquidationQueryUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.getLiquidation("schedule-1"));
    }

    @Test
    void shouldRejectReceiveScheduleUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createReceiveSchedule(
                new CelcoinPixAutoReceiveScheduleRequest(
                        "account-1",
                        "consent-1",
                        BigDecimal.TEN,
                        LocalDate.now().plusDays(7),
                        "MONTHLY",
                        10,
                        null),
                "receive-1"));
    }

    @Test
    void shouldRejectRetryUntilOfficialContractIsAdded() {
        assertPendingContract(
                () -> client.retryReceipt(new CelcoinPixAutoRetryRequest("schedule-1", 2, "retry", null), "retry-1"));
    }

    @Test
    void shouldRejectRecurrenceCancellationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.cancelRecurrence("recurrence-1", "cancel-3"));
    }

    @Test
    void shouldRejectRejectionReasonsUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.rejectionReasons());
    }

    private void assertPendingContract(ThrowingCallable callable) {
        assertThatThrownBy(callable::call)
                .isInstanceOf(CelcoinIntegrationException.class)
                .hasMessageContaining("Celcoin Pix Automático endpoint path is not configured");
    }

    @FunctionalInterface
    private interface ThrowingCallable {
        void call();
    }
}
