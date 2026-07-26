package com.brunopedraca.celcoin.pix;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashInAccountRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashInCautionaryBlockRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashInDueDateQrCodeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashInKeyRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashInStaticChargeRequest;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinPixClientPendingContractTest {
    private CelcoinPixClient client;

    @BeforeEach
    void setUp() {
        client = new CelcoinPixClient(null);
    }

    @Test
    void shouldRejectAccountCashInUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createAccountCashIn(
                new CelcoinPixCashInAccountRequest("0001", "12345", BigDecimal.TEN, "cash-in", null), "cash-in-1"));
    }

    @Test
    void shouldRejectRandomKeyCashInUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createRandomKeyCashIn(
                new CelcoinPixCashInKeyRequest("account-1", "RANDOM", null, BigDecimal.TEN, "cash-in", null),
                "cash-in-2"));
    }

    @Test
    void shouldRejectIndividualKeyCashInUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createIndividualKeyCashIn(
                new CelcoinPixCashInKeyRequest("account-1", "CPF", "12345678901", BigDecimal.TEN, "cash-in", null),
                "cash-in-3"));
    }

    @Test
    void shouldRejectStaticChargeCashInUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createStaticChargeCashIn(
                new CelcoinPixCashInStaticChargeRequest(
                        "account-1", BigDecimal.TEN, "cash-in", "12345678901", "Maria Silva", null),
                "cash-in-4"));
    }

    @Test
    void shouldRejectDueDateQrCodeCashInUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createDueDateQrCodeCashIn(
                new CelcoinPixCashInDueDateQrCodeRequest(
                        "account-1", BigDecimal.TEN, OffsetDateTime.now().plusDays(1), "cash-in", null, null, null),
                "cash-in-5"));
    }

    @Test
    void shouldRejectCashInReceiptsUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.listCashInReceipts("account-1"));
    }

    @Test
    void shouldRejectCashInCautionaryBlockUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createCashInCautionaryBlock(
                new CelcoinPixCashInCautionaryBlockRequest("transaction-1", "suspected fraud", null), "block-1"));
    }

    private void assertPendingContract(ThrowingCallable callable) {
        assertThatThrownBy(callable::call)
                .isInstanceOf(CelcoinIntegrationException.class)
                .hasMessageContaining("Celcoin Pix endpoint path is not configured");
    }

    @FunctionalInterface
    private interface ThrowingCallable {
        void call();
    }
}
