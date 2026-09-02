package com.brunopedraca.celcoin.bff.v1.boleto;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoBarCode;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPaymentRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPaymentResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(path = "/mobile/v1/boleto-payments", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileBoletoController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobileBoletoController(CelcoinClient client, MobileAccountAuthorizationService authorization) { this.client = client; this.authorization = authorization; }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public PaymentResponse pay(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody PaymentRequest request) {
        authorization.requireRisk(request.accountId());
        CelcoinBoletoPaymentResponse response = client.boletos().pay(new CelcoinBoletoPaymentRequest(request.clientRequestId(), request.amount(), request.accountId(), request.authorizationId(), null, new CelcoinBoletoBarCode(request.barCodeType(), request.digitableLine(), request.barCode())), key);
        return new PaymentResponse(response.paymentId(), response.status(), response.amount());
    }
    @GetMapping("/{boletoId}")
    public BoletoResponse get(@PathVariable String boletoId, @RequestParam String accountId) {
        authorization.requireRead(accountId);
        CelcoinBoletoResponse response = client.boletos().get(boletoId);
        return new BoletoResponse(response.boletoId(), response.status(), response.digitableLine());
    }
    public record PaymentRequest(@NotBlank String accountId, @NotBlank String clientRequestId, BigDecimal amount, Long authorizationId, Integer barCodeType, String digitableLine, String barCode) {}
    public record PaymentResponse(String paymentId, String status, BigDecimal amount) {}
    public record BoletoResponse(String boletoId, String status, String digitableLine) {}
}
