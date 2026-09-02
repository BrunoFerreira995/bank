package com.brunopedraca.celcoin.bff.v1.transfers;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinTedCreditParty;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinTedTransferRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinTedTransferResponse;
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
@RequestMapping(path = "/mobile/v1/transfers", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileTransferController {
    private final CelcoinClient client;
    private final MobileAccountAuthorizationService authorization;
    public MobileTransferController(CelcoinClient client, MobileAccountAuthorizationService authorization) { this.client = client; this.authorization = authorization; }
    @PostMapping(path = "/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TransferResponse internal(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody InternalTransferRequest request) {
        authorization.requireRisk(request.sourceAccountId());
        CelcoinInternalTransferResponse response = client.accounts().transfer(new CelcoinInternalTransferRequest(request.sourceAccountId(), request.targetAccountId(), request.amount(), request.description(), request.clientRequestId(), null), key);
        return new TransferResponse(response.transferId(), response.status(), response.amount());
    }
    @PostMapping(path = "/ted", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TransferResponse ted(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody TedRequest request) {
        authorization.requireRisk(request.sourceAccountId());
        CelcoinTedTransferResponse response = client.accounts().transferTed(new CelcoinTedTransferRequest(request.sourceAccountId(), request.amount(), request.clientFinality(), new CelcoinTedCreditParty(request.bank(), request.account(), request.branch(), request.taxId(), request.name(), request.accountType(), request.personType()), request.clientCode(), request.description()), key);
        return new TransferResponse(response.body() == null ? null : response.body().id(), response.status(), response.body() == null ? null : response.body().amount());
    }
    public record InternalTransferRequest(@NotBlank String sourceAccountId, @NotBlank String targetAccountId, BigDecimal amount, String description, String clientRequestId) {}
    public record TedRequest(@NotBlank String sourceAccountId, BigDecimal amount, @NotBlank String clientFinality, @NotBlank String bank, @NotBlank String account, @NotBlank String branch, @NotBlank String taxId, @NotBlank String name, @NotBlank String accountType, @NotBlank String personType, String clientCode, String description) {}
    public record TransferResponse(String transferId, String status, BigDecimal amount) {}
}
