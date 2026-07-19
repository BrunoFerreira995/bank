package com.brunopedraca.celcoin.pix;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class PixDtos {
    private PixDtos() {}

    public record CelcoinPixQrCodeRequest(BigDecimal amount, String description, Map<String, Object> metadata) {}

    public record CelcoinPixQrCodeResponse(String qrCodeId, String emv, String status, Map<String, Object> raw) {}

    public record CelcoinPixPaymentRequest(@NotBlank String accountId, @NotBlank String pixKey, BigDecimal amount, String description) {}

    public record CelcoinPixPaymentResponse(String transactionId, String status, Map<String, Object> raw) {}

    public record CelcoinPixStatusResponse(String transactionId, String status, OffsetDateTime updatedAt, Map<String, Object> raw) {}

    public record CelcoinPixRefundRequest(@NotBlank String transactionId, BigDecimal amount, String reason) {}

    public record CelcoinPixRefundResponse(String refundId, String status, Map<String, Object> raw) {}

    public record CelcoinPixKeyRequest(@NotBlank String accountId, @NotBlank String keyType, String key) {}

    public record CelcoinPixKeyResponse(String keyId, String keyType, String key, String status, Map<String, Object> raw) {}

    public record CelcoinPixScheduleRequest(CelcoinPixPaymentRequest payment, OffsetDateTime scheduledAt) {}

    public record CelcoinPixScheduleResponse(String scheduleId, String status, Map<String, Object> raw) {}

    public record CelcoinPixParticipantResponse(String ispb, String name, Map<String, Object> raw) {}

    public record CelcoinPixSplitRequest(CelcoinPixQrCodeRequest qrCode, List<Map<String, Object>> splits) {}
}
