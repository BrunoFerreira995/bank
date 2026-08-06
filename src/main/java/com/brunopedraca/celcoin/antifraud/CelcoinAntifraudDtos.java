package com.brunopedraca.celcoin.antifraud;

import java.time.OffsetDateTime;
import java.util.Map;

public final class CelcoinAntifraudDtos {
    private CelcoinAntifraudDtos() {}

    public record RiskAssessment(
            String taxId,
            OffsetDateTime timestamp,
            RiskSummary summary,
            Map<String, Object> services,
            java.util.List<Map<String, Object>> errors) {}

    public record RiskSummary(
            String riskLevel,
            Boolean shouldBlockDirectory,
            Boolean shouldBlockRes6,
            Boolean shouldBlockAny) {}
}
