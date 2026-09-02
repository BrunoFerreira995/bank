package com.brunopedraca.celcoin.bff.v1.topup;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.topup.TopupDtos.TopupRequest;
import com.brunopedraca.celcoin.topup.TopupDtos.TopupResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile/v1/topups", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileTopupController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobileTopupController(CelcoinClient client, MobileAccountAuthorizationService authorization) { this.client = client; this.authorization = authorization; }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public TopupResult reserve(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody TopupInput input) {
        authorization.requireRisk(input.accountId());
        TopupResponse response = client.topups().reserve(new TopupRequest(input.accountId(), input.clientRequestId(), input.amount(), input.providerId(), null, null, input.data()), key);
        return new TopupResult(response.transactionId(), response.status(), response.amount());
    }
    public record TopupInput(@NotBlank String accountId, @NotBlank String clientRequestId, BigDecimal amount, Integer providerId, Map<String, Object> data) {}
    public record TopupResult(String transactionId, String status, BigDecimal amount) {}
}
