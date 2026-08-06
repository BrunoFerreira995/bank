package com.brunopedraca.celcoin.antifraud;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.Map;

public final class CelcoinAntifraudDtos {
    private CelcoinAntifraudDtos() {}

    public record RiskAssessment(
            @JsonProperty("taxid")
            String taxId,
            OffsetDateTime timestamp,
            RiskSummary summary,
            Map<String, Object> services,
            java.util.List<Map<String, Object>> errors) {}

    public record RiskSummary(
            @JsonProperty("risk_level")
            String riskLevel,
            @JsonProperty("should_block_directory")
            Boolean shouldBlockDirectory,
            @JsonProperty("should_block_res6")
            Boolean shouldBlockRes6,
            @JsonProperty("should_block_any")
            Boolean shouldBlockAny) {}
}
