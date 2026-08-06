package com.brunopedraca.celcoin.openfinance;

import java.util.List;

/** Official sandbox scenario catalog for payment journeys. */
public final class OpenFinanceTestScenarios {
    private OpenFinanceTestScenarios() {}

    public record Scenario(String id, String objective, String stage, String expectedResult,
            String expectedError, boolean retryable) {}

    public static List<Scenario> paymentJourneys() {
        return List.of(
                new Scenario("JSR-LIM-001", "Success at the maximum linked Pix limit", "PIX", "CONSUMED", null, false),
                new Scenario("JSR-LIM-001.1", "Success at the linked and daily limit", "PIX", "CONSUMED", null, false),
                new Scenario("JSR-LIM-002", "Payment above the linked limit", "PIX", "RJCT", "VALOR_ACIMA_LIMITE", false),
                new Scenario("JSR-SAL-003", "Insufficient balance", "PIX", "RJCT", "SALDO_INSUFICIENTE", true),
                new Scenario("JSR-MIX-004", "Limit and balance failure precedence", "PIX", "RJCT", null, false),
                new Scenario("JSR-LIMD-005", "Payment above the daily limit", "PIX", "RJCT", "LIMITE_DIARIO_EXCEDIDO", false),
                new Scenario("JSR-LIMN-006", "Payment above the night limit", "PIX", "RJCT", "LIMITE_NOTURNO_EXCEDIDO", false),
                new Scenario("JSR-CONS-007", "Expired payment consent", "PIX", "RJCT", "CONSENTIMENTO_EXPIRADO", false),
                new Scenario("JSR-CONS-008", "Revoked payment consent", "PIX", "RJCT", "CONSENTIMENTO_REVOGADO", false),
                new Scenario("JSR-FIDO-011", "Device binding rejected by timeout", "ENROLLMENT", "REJECTED", "TIMEOUT", false));
    }
}
