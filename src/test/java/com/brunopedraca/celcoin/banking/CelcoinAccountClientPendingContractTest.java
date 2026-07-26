package com.brunopedraca.celcoin.banking;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountClosureRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountCustomerUpdateRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountFinancialInformation;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountFinancialInformationRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountListRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountMonitoringRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountStatusUpdateRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinCoreAccountRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinJudicialBlockRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinSandboxBalanceRequest;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinAccountClientPendingContractTest {
    private CelcoinAccountClient client;

    @BeforeEach
    void setUp() {
        client = new CelcoinAccountClient(null);
    }

    @Test
    void shouldRejectCoreAccountCreationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createCoreAccount(
                new CelcoinCoreAccountRequest("12345678901", "Maria Silva", "PERSON", null, null, null, null),
                "account-1"));
    }

    @Test
    void shouldRejectFinancialInformationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.updateFinancialInformation(new CelcoinAccountFinancialInformationRequest(
                "account-1",
                new CelcoinAccountFinancialInformation(
                        BigDecimal.valueOf(5000), null, BigDecimal.valueOf(10000), "Engineer", null, "Salary", false))));
    }

    @Test
    void shouldRejectCustomerUpdateUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.updateCustomer(
                new CelcoinAccountCustomerUpdateRequest("account-1", "Maria Silva", "maria@example.com", null, null, null)));
    }

    @Test
    void shouldRejectAccountClosureUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.closeAccount(new CelcoinAccountClosureRequest("account-1", "requested", null)));
    }

    @Test
    void shouldRejectAccountDeactivationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.deactivateAccount(new CelcoinAccountClosureRequest("account-1", "risk", null)));
    }

    @Test
    void shouldRejectAccountListingUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.listAccounts(new CelcoinAccountListRequest(null, "PERSON", "ACTIVE", 0, 20)));
    }

    @Test
    void shouldRejectAccountCountingUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.countAccounts(new CelcoinAccountListRequest(null, null, "ACTIVE", null, null)));
    }

    @Test
    void shouldRejectJudicialBlockUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createJudicialBlock(
                new CelcoinJudicialBlockRequest("account-1", BigDecimal.TEN, "0000000-00.2026.0.00.0000", "court order"),
                "block-1"));
    }

    @Test
    void shouldRejectStatusUpdateUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.updateStatus(new CelcoinAccountStatusUpdateRequest("account-1", "BLOCKED", "risk")));
    }

    @Test
    void shouldRejectSandboxBalanceUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.addSandboxBalance(new CelcoinSandboxBalanceRequest("account-1", BigDecimal.TEN, "sandbox")));
    }

    @Test
    void shouldRejectMonitoringUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createMonitoring(new CelcoinAccountMonitoringRequest("account-1", "KYC", null)));
    }

    @Test
    void shouldRejectMonitoringSimulationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.simulateMonitoring("monitoring-1", "APPROVED"));
    }

    private void assertPendingContract(ThrowingCallable callable) {
        assertThatThrownBy(callable::call)
                .isInstanceOf(CelcoinIntegrationException.class)
                .hasMessageContaining("Celcoin account endpoint path is not configured");
    }

    @FunctionalInterface
    private interface ThrowingCallable {
        void call();
    }
}
