package com.brunopedraca.celcoin.indirectpix;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class IndirectPixDtos {
    private IndirectPixDtos() {}

    public record CelcoinIndirectPixResponse(Map<String, Object> raw) {}

    public record CelcoinIndirectDictLookupRequest(
            @NotBlank String payerId, @NotBlank String key, String endToEndId, Boolean includeStatistics) {}

    public record CelcoinIndirectDictKeyCheckRequest(List<String> keys) {}

    public record CelcoinIndirectDictKeyRequest(Map<String, Object> payload) {}

    public record CelcoinIndirectDictDeleteRequest(@NotBlank String key, Map<String, Object> payload) {}

    public record CelcoinIndirectClaimRequest(Map<String, Object> payload) {}

    public record CelcoinIndirectInfractionRequest(Map<String, Object> payload) {}

    public record CelcoinIndirectInfractionListRequest(
            Boolean isReporter,
            Boolean isCounterparty,
            String fundsRecoveryId,
            String status,
            OffsetDateTime dateHourChangeStart,
            OffsetDateTime dateHourChangeEnd,
            Integer pageSize,
            Integer page) {}

    public record CelcoinIndirectInfractionCloseRequest(Map<String, Object> payload) {}

    public record CelcoinIndirectMedRefundRequest(Map<String, Object> payload) {}

    public record CelcoinIndirectMedCloseRequest(
            String transactionId,
            BigDecimal refundAmount,
            String refundAnalysisResult,
            String refundAnalysisDetails,
            String refundRejectionReason) {}
}
