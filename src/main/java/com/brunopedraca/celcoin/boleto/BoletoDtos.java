package com.brunopedraca.celcoin.boleto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public final class BoletoDtos {
    private BoletoDtos() {}

    public record CelcoinBoletoRequest(
            @NotBlank String accountId, BigDecimal amount, LocalDate dueDate, Map<String, Object> payer) {}

    /** Contrato de emissão BaaS: POST /baas/v2/charge. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinBoletoIssueRequest(
            @NotBlank String externalId,
            Integer merchantCatagoryCode,
            Integer expirationAfterPayment,
            @JsonProperty("duedate") @NotBlank String dueDate,
            BigDecimal amount,
            String key,
            String invoiceNumber,
            Map<String, Object> debtor,
            Map<String, Object> receiver,
            Map<String, Object> instructions,
            List<Map<String, Object>> split,
            Integer bankEmissor,
            List<String> informations,
            String chargeType) {}

    public record CelcoinBoletoIssueResponse(
            String version, String status, CelcoinBoletoIssueBody body, Map<String, Object> error) {}

    public record CelcoinBoletoIssueBody(String transactionId) {}

    public record CelcoinBoletoApiEnvelope(
            String version, String status, Map<String, Object> body, Map<String, Object> error) {}

    public record CelcoinBoletoResponse(
            String boletoId, String status, String digitableLine, Map<String, Object> raw) {}

    public record CelcoinBoletoPeriodRequest(
            LocalDate startDate,
            LocalDate endDate,
            String status,
            Integer page,
            Integer limit,
            String sort,
            String receiverDocument,
            String receiverAccount,
            String debtorDocument,
            String dateType) {
        public CelcoinBoletoPeriodRequest(LocalDate startDate, LocalDate endDate, String status) {
            this(startDate, endDate, status, null, null, null, null, null, null, null);
        }
    }

    public record CelcoinBoletoListResponse(List<CelcoinBoletoResponse> boletos) {}

    /** Consulta/autorização do código de barras antes da efetivação. */
    public record CelcoinBoletoAuthorizationRequest(
            String externalTerminal, Long externalNSU, @Valid CelcoinBoletoBarCode barCode) {}

    public record CelcoinBoletoBarCode(Integer type, String digitable, String barCode) {}

    public record CelcoinBoletoAuthorizationResponse(
            Long transactionId, String status, BigDecimal totalUpdated, Map<String, Object> raw) {}

    /** Efetivação de pagamento em uma conta BaaS. */
    public record CelcoinBoletoPaymentRequest(
            @NotBlank String clientRequestId,
            BigDecimal amount,
            @JsonProperty("account") @NotBlank String accountId,
            Long transactionIdAuthorize,
            List<CelcoinBoletoTag> tags,
            @Valid CelcoinBoletoBarCode barCodeInfo) {}

    public record CelcoinBoletoTag(String key, String value) {}

    public record CelcoinBoletoPaymentResponse(
            String paymentId, String clientRequestId, String status, BigDecimal amount, Map<String, Object> raw) {}
}
