package com.brunopedraca.celcoin.topup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class TopupDtos {
    private TopupDtos() {}

    public record ProviderQuery(Integer stateCode, Integer type, Integer category) {}
    public record Provider(String name, Integer providerId, Integer category, Integer operationType,
            BigDecimal minValue, BigDecimal maxValue, Map<String, Object> raw) {}
    public record ProviderListResponse(List<Provider> providers, Map<String, Object> raw) {}
    public record ProviderValuesQuery(@NotNull Integer stateCode, @NotNull Integer providerId) {}
    public record ProviderValue(String productName, BigDecimal cost, BigDecimal valueBonus,
            BigDecimal minValue, BigDecimal maxValue, Integer code, Map<String, Object> raw) {}
    public record ProviderValuesResponse(List<ProviderValue> values, String externalNsuQuery,
            String errorCode, String message, Integer status, Map<String, Object> raw) {}

    public record TopupRequest(@NotBlank String account, @NotBlank String clientRequestId,
            @NotNull BigDecimal amount, @NotNull Integer providerId, String signerCode,
            List<TopupTag> tags, Map<String, Object> topupData) {}
    public record TopupTag(@NotBlank String key, @NotBlank String value) {}
    public record TopupResponse(String transactionId, String clientRequestId, String status,
            BigDecimal amount, Integer providerId, Map<String, Object> raw) {}
    public record CaptureRequest(String externalTerminal, Long externalNsu) {}
}
