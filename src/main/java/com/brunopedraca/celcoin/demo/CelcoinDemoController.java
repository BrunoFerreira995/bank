package com.brunopedraca.celcoin.demo;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.auth.CelcoinTokenResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixPaymentRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixPaymentResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixQrCodeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixQrCodeResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixStatusResponse;
import java.time.LocalDate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@ConditionalOnProperty(prefix = "celcoin", name = "demo-enabled", havingValue = "true")
public class CelcoinDemoController {
    private final CelcoinClient celcoinClient;

    public CelcoinDemoController(CelcoinClient celcoinClient) {
        this.celcoinClient = celcoinClient;
    }

    @PostMapping("/auth/token")
    public CelcoinTokenResponse token() {
        return celcoinClient.authentication().getToken();
    }

    @GetMapping("/accounts/{accountId}/balance")
    public CelcoinBalanceResponse balance(@PathVariable String accountId) {
        return celcoinClient.accounts().getBalance(accountId);
    }

    @GetMapping("/accounts/{accountId}/statement")
    public CelcoinStatementResponse statement(@PathVariable String accountId) {
        return celcoinClient
                .accounts()
                .getStatement(new CelcoinStatementRequest(accountId, LocalDate.now().minusDays(30), LocalDate.now()));
    }

    @PostMapping("/pix/cash-out")
    public CelcoinPixPaymentResponse pixCashOut(
            @RequestBody CelcoinPixPaymentRequest request, @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        return celcoinClient.pix().cashOut(request, key);
    }

    @PostMapping("/pix/qr-code")
    public CelcoinPixQrCodeResponse pixQrCode(
            @RequestBody CelcoinPixQrCodeRequest request, @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        return celcoinClient.pix().createQrCode(request, key);
    }

    @GetMapping("/pix/{transactionId}/status")
    public CelcoinPixStatusResponse pixStatus(@PathVariable String transactionId) {
        return celcoinClient.pix().getStatus(transactionId);
    }

    @PostMapping("/boletos")
    public CelcoinBoletoResponse issueBoleto(
            @RequestBody CelcoinBoletoRequest request, @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        return celcoinClient.boletos().issue(request, key);
    }

    @GetMapping("/boletos/{id}")
    public CelcoinBoletoResponse getBoleto(@PathVariable String id) {
        return celcoinClient.boletos().get(id);
    }
}
