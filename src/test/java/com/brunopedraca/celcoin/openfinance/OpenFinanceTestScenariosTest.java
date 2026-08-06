package com.brunopedraca.celcoin.openfinance;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.itp.ItpStateMachine;
import com.brunopedraca.celcoin.jsr.CelcoinJsrClient;
import com.brunopedraca.celcoin.jsr.CelcoinJsrDtos;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OpenFinanceTestScenariosTest {
    @Test
    void exposesAllOfficialPaymentJourneyScenarios() {
        var scenarios = OpenFinanceTestScenarios.paymentJourneys();
        assertThat(scenarios).hasSize(10);
        assertThat(scenarios).extracting(OpenFinanceTestScenarios.Scenario::id)
                .containsExactly("JSR-LIM-001", "JSR-LIM-001.1", "JSR-LIM-002", "JSR-SAL-003",
                        "JSR-MIX-004", "JSR-LIMD-005", "JSR-LIMN-006", "JSR-CONS-007",
                        "JSR-CONS-008", "JSR-FIDO-011");
    }

    @Test
    void modelsSuccessAndFailureTransitions() {
        assertThat(ItpStateMachine.canTransition(
                ItpStateMachine.ConsentState.AUTHORISED, ItpStateMachine.ConsentState.CONSUMED)).isTrue();
        assertThat(ItpStateMachine.canTransition(
                ItpStateMachine.PaymentState.PDNG, ItpStateMachine.PaymentState.RJCT)).isTrue();
        assertThat(ItpStateMachine.canTransition(
                ItpStateMachine.PaymentState.RJCT, ItpStateMachine.PaymentState.ACSC)).isFalse();
    }

    @Test
    void validatesFidoTimeoutPayloadShapeWithoutCallingSandbox() {
        var client = new CelcoinJsrClient(null);
        var result = client.validateFidoBiometry(Map.of(
                "id", "credential", "rawId", "raw", "type", "public-key",
                "response", Map.of("clientDataJSON", "encoded")));
        assertThat(result.valid()).isTrue();
        assertThat(result.reason()).contains("device-side");
    }
}
