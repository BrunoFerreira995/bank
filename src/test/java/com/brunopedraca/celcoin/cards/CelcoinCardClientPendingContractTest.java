package com.brunopedraca.celcoin.cards;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardAccountRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardAddressRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardCustomerUpdateRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardIssueRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardListRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardPhoneUpdateRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardPinUpdateRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardStatusUpdateRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardTransactionSimulationRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardWebhookRequest;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardWebhookResendRequest;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinCardClientPendingContractTest {
    private CelcoinCardClient client;

    @BeforeEach
    void setUp() {
        client = new CelcoinCardClient(null);
    }

    @Test
    void shouldRejectCardOperationsUntilOfficialContractIsAdded() {
        List<ThrowingCallable> operations = List.of(
                () -> client.createCardAccount(accountRequest(), "card-account-1"),
                () -> client.getCardAccount("card-account-1"),
                () -> client.getCardAccountLimits("card-account-1"),
                () -> client.updateCustomer(new CelcoinCardCustomerUpdateRequest(
                        "card-account-1", "Maria Silva", "maria@example.com", null, null, null)),
                () -> client.updatePhone(new CelcoinCardPhoneUpdateRequest("card-account-1", "+5511999999999")),
                () -> client.cancelAccountAndCards("card-account-1", "requested", "cancel-card-account-1"),
                () -> client.updateAddress(new CelcoinCardAddressRequest("card-account-1", null)),
                () -> client.issueCard(cardRequest(), "issue-card-1"),
                () -> client.reissueCard(cardRequest(), "reissue-card-1"),
                () -> client.getTracking("card-1"),
                () -> client.simulateTracking("card-1", "DELIVERED"),
                () -> client.activateCard("card-1", "activate-card-1"),
                () -> client.updateStatus(new CelcoinCardStatusUpdateRequest("card-1", "BLOCKED", "risk")),
                () -> client.getSensitiveData("card-1"),
                () -> client.listCards(new CelcoinCardListRequest("card-account-1", "ACTIVE", "PHYSICAL", 0, 20)),
                () -> client.updatePin(new CelcoinCardPinUpdateRequest("card-1", "1234", "1234"), "pin-1"),
                () -> client.simulateTransaction(
                        new CelcoinCardTransactionSimulationRequest("card-1", BigDecimal.TEN, "Merchant", "5411", null),
                        "simulation-1"),
                () -> client.createWebhook(new CelcoinCardWebhookRequest("card.transaction", "https://example.com/webhook", null, null)),
                () -> client.updateWebhook(new CelcoinCardWebhookRequest("card.transaction", "https://example.com/webhook", null, null)),
                () -> client.getWebhookTemplate("card.transaction"),
                () -> client.resendPendingWebhook(new CelcoinCardWebhookResendRequest("event-1"), "resend-webhook-1"),
                () -> client.getPostpaidInvoice("card-account-1", "invoice-1"));

        operations.forEach(this::assertPendingContract);
    }

    private void assertPendingContract(ThrowingCallable callable) {
        assertThatThrownBy(callable::call)
                .isInstanceOf(CelcoinIntegrationException.class)
                .hasMessageContaining("Celcoin card endpoint path is not configured");
    }

    private CelcoinCardAccountRequest accountRequest() {
        return new CelcoinCardAccountRequest("12345678901", "Maria Silva", "maria@example.com", "+5511999999999", null, null);
    }

    private CelcoinCardIssueRequest cardRequest() {
        return new CelcoinCardIssueRequest("card-account-1", "Maria Silva", "PHYSICAL", null, null);
    }

    @FunctionalInterface
    private interface ThrowingCallable {
        void call();
    }
}
