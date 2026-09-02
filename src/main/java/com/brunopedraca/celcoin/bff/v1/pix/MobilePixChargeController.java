package com.brunopedraca.celcoin.bff.v1.pix;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.pix.PixDtos.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/** BFF-owned charge contracts. Account authorization is checked before a Pix key is used. */
@RestController
@RequestMapping(path = "/mobile/v1/pix/charges", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobilePixChargeController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobilePixChargeController(CelcoinClient client, MobileAccountAuthorizationService authorization) { this.client = client; this.authorization = authorization; }
    @PostMapping(path = "/immediate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChargeResponse immediate(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody ImmediateCharge request) {
        authorization.requireWrite(request.accountId());
        CelcoinPixQrCodeResponse response = client.pix().createQrCode(new CelcoinPixQrCodeRequest(request.pixKey(), request.amount() == null ? null : request.amount().toPlainString(), merchant(request), request.expiration(), request.clientRequestId(), request.payerName(), request.payerCpf(), request.payerCnpj(), request.payerQuestion()), key);
        return new ChargeResponse(response.transactionId(), response.transactionIdentification(), response.status(), response.emv(), response.location());
    }
    @PostMapping(path = "/static", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object statik(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody StaticCharge request) {
        authorization.requireWrite(request.accountId());
        return client.pix().createStaticChargeCashIn(new CelcoinPixStaticChargeRequest(request.pixKey(), request.amount(), merchant(request), request.transactionIdentification()), key);
    }
    @PostMapping(path = "/duedate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object dueDate(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody DueDateCharge request) {
        authorization.requireWrite(request.accountId());
        return client.pix().createDueDateQrCodeCashIn(new CelcoinPixDueDateQrCodeRequest(request.pixKey(), request.clientRequestId(), request.amount() == null ? null : request.amount().toPlainString(), request.expirationAfterPayment(), request.dueDate(), null, request.debtor(), request.receiver(), null, null, null), key);
    }
    private static CelcoinPixMerchant merchant(BaseCharge value) { return new CelcoinPixMerchant(value.merchantCategoryCode() == null ? null : value.merchantCategoryCode().toString(), value.postalCode(), value.city(), value.merchantName()); }
    interface BaseCharge { Integer merchantCategoryCode(); String postalCode(); String city(); String merchantName(); }
    public record ImmediateCharge(@NotBlank String accountId, @NotBlank String pixKey, BigDecimal amount, Integer expiration, @NotBlank String clientRequestId, String payerName, String payerCpf, String payerCnpj, String payerQuestion, Integer merchantCategoryCode, String postalCode, String city, String merchantName) implements BaseCharge {}
    public record StaticCharge(@NotBlank String accountId, @NotBlank String pixKey, BigDecimal amount, String transactionIdentification, Integer merchantCategoryCode, String postalCode, String city, String merchantName) implements BaseCharge {}
    public record DueDateCharge(@NotBlank String accountId, @NotBlank String pixKey, @NotBlank String clientRequestId, BigDecimal amount, Integer expirationAfterPayment, OffsetDateTime dueDate, CelcoinPixPerson debtor, CelcoinPixPerson receiver, Integer merchantCategoryCode, String postalCode, String city, String merchantName) implements BaseCharge {}
    public record ChargeResponse(String transactionId, String transactionIdentification, String status, String emv, String location) {}
}
