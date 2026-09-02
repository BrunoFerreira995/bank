package com.brunopedraca.celcoin.bff.v1.pix;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashOutKeyRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashOutAccountRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashOutStaticQrCodeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashOutDynamicQrCodeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixPaymentResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile/v1/pix/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobilePixPaymentController {
    private final CelcoinClient client;
    private final MobileAccountAuthorizationService authorization;
    public MobilePixPaymentController(CelcoinClient client, MobileAccountAuthorizationService authorization) { this.client = client; this.authorization = authorization; }
    @PostMapping(path = "/key", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PixPaymentResponse payByKey(@RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey, @Valid @RequestBody PixKeyPaymentRequest request) {
        authorization.requireRisk(request.sourceAccountId());
        CelcoinPixPaymentResponse response = client.pix().cashOutByKey(new CelcoinPixCashOutKeyRequest(request.sourceAccountId(), request.key(), request.bank(), request.name(), request.amount(), request.clientCode(), request.description()), idempotencyKey);
        return new PixPaymentResponse(response.status(), response.body() == null ? null : response.body().id(), response.body() == null ? null : response.body().amount());
    }
    @PostMapping(path = "/bank-details", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PixPaymentResponse payByBankDetails(@RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey, @Valid @RequestBody PixBankPaymentRequest request) {
        authorization.requireRisk(request.sourceAccountId());
        CelcoinPixPaymentResponse response = client.pix().cashOutToAccount(new CelcoinPixCashOutAccountRequest(request.sourceAccountId(), request.branch(), request.account(), request.document(), request.name(), request.amount(), request.description(), null), idempotencyKey);
        return response(response);
    }
    @PostMapping(path = "/qr/static", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PixPaymentResponse payStaticQr(@RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey, @Valid @RequestBody PixQrPaymentRequest request) {
        authorization.requireRisk(request.sourceAccountId());
        return response(client.pix().cashOutStaticQrCode(new CelcoinPixCashOutStaticQrCodeRequest(request.sourceAccountId(), request.emv(), request.amount(), request.description()), idempotencyKey));
    }
    @PostMapping(path = "/qr/dynamic", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PixPaymentResponse payDynamicQr(@RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey, @Valid @RequestBody PixQrPaymentRequest request) {
        authorization.requireRisk(request.sourceAccountId());
        return response(client.pix().cashOutDynamicQrCode(new CelcoinPixCashOutDynamicQrCodeRequest(request.sourceAccountId(), request.emv(), request.description()), idempotencyKey));
    }
    private static PixPaymentResponse response(CelcoinPixPaymentResponse response) { return new PixPaymentResponse(response.status(), response.body() == null ? null : response.body().id(), response.body() == null ? null : response.body().amount()); }
    public record PixKeyPaymentRequest(@NotBlank String sourceAccountId, @NotBlank String key, String bank, String name, BigDecimal amount, String clientCode, String description) {}
    public record PixBankPaymentRequest(@NotBlank String sourceAccountId, @NotBlank String branch, @NotBlank String account, String document, @NotBlank String name, BigDecimal amount, String description) {}
    public record PixQrPaymentRequest(@NotBlank String sourceAccountId, @NotBlank String emv, BigDecimal amount, String description) {}
    public record PixPaymentResponse(String status, String transactionId, BigDecimal amount) {}
}
