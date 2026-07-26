package com.brunopedraca.celcoin.onboarding;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycAddress;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessOwner;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycFinancialInformation;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycFinancialInformationRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycWebhookSubscriptionRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinOnboardingClientPendingContractTest {
    private CelcoinOnboardingClient client;

    @BeforeEach
    void setUp() {
        client = new CelcoinOnboardingClient(null);
    }

    @Test
    void shouldRejectPersonAccountOpeningUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createPersonAccount(personRequest(), "idem-1"));
    }

    @Test
    void shouldRejectBusinessAccountOpeningUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createBusinessAccount(businessRequest(), "idem-2"));
    }

    @Test
    void shouldRejectKycAccountStatusUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.getStatus("onboarding-1"));
    }

    @Test
    void shouldRejectFinancialInformationUpdateUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.updateFinancialInformation(
                new CelcoinKycFinancialInformationRequest("onboarding-1", financialInformation())));
    }

    @Test
    void shouldRejectWebhookSubscriptionUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.createWebhookSubscription(
                new CelcoinKycWebhookSubscriptionRequest("onboarding.status.updated", "https://example.com/webhook", null, null)));
    }

    @Test
    void shouldRejectStatusSimulationUntilOfficialContractIsAdded() {
        assertPendingContract(() -> client.simulateStatus("onboarding-1", "APPROVED"));
    }

    private void assertPendingContract(ThrowingCallable callable) {
        assertThatThrownBy(callable::call)
                .isInstanceOf(CelcoinIntegrationException.class)
                .hasMessageContaining("Celcoin onboarding KYC endpoint path is not configured");
    }

    private CelcoinKycPersonAccountRequest personRequest() {
        return new CelcoinKycPersonAccountRequest(
                "12345678901",
                "Maria Silva",
                LocalDate.of(1990, 1, 10),
                "maria@example.com",
                "+5511999999999",
                address(),
                financialInformation(),
                List.of(),
                null);
    }

    private CelcoinKycBusinessAccountRequest businessRequest() {
        return new CelcoinKycBusinessAccountRequest(
                "12345678000190",
                "Empresa Exemplo LTDA",
                "Empresa Exemplo",
                LocalDate.of(2020, 5, 15),
                "contato@example.com",
                "+551133333333",
                address(),
                financialInformation(),
                List.of(new CelcoinKycBusinessOwner(
                        "12345678901",
                        "Maria Silva",
                        LocalDate.of(1990, 1, 10),
                        BigDecimal.valueOf(100),
                        "maria@example.com",
                        "+5511999999999",
                        address(),
                        financialInformation(),
                        List.of())),
                List.of(),
                null);
    }

    private CelcoinKycAddress address() {
        return new CelcoinKycAddress(
                "Rua Exemplo", "100", null, "Centro", "Sao Paulo", "SP", "01001000", "BR");
    }

    private CelcoinKycFinancialInformation financialInformation() {
        return new CelcoinKycFinancialInformation(
                BigDecimal.valueOf(5000), null, BigDecimal.valueOf(10000), "Engineer", null, "Salary", false);
    }

    @FunctionalInterface
    private interface ThrowingCallable {
        void call();
    }
}
