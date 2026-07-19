package com.brunopedraca.celcoin.boleto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class BoletoDtos {
    private BoletoDtos() {}

    public record CelcoinBoletoRequest(@NotBlank String accountId, BigDecimal amount, LocalDate dueDate, Map<String, Object> payer) {}

    public record CelcoinBoletoResponse(String boletoId, String status, String digitableLine, Map<String, Object> raw) {}

    public record CelcoinBoletoPeriodRequest(LocalDate startDate, LocalDate endDate, String status) {}

    public record CelcoinBoletoListResponse(List<CelcoinBoletoResponse> boletos) {}
}
