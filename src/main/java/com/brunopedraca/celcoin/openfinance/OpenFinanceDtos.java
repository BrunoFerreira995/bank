package com.brunopedraca.celcoin.openfinance;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public final class OpenFinanceDtos {
    private OpenFinanceDtos() {}

    public record CelcoinOpenFinanceBrand(String id, String name, String logoUri, String status,
            Map<String, Object> raw) {}

    public record CelcoinOpenFinanceConsentRequest(@NotBlank String brandId, Map<String, Object> data) {}

    public record CelcoinOpenFinanceCallbackRequest(
            @NotBlank String code, @NotBlank String state, @NotBlank String idToken) {}

    public record CelcoinOpenFinancePageRequest(Integer page, Integer pageSize, String paginationKey) {}

    public record CelcoinOpenFinanceResponse(Map<String, Object> raw) {}
}
