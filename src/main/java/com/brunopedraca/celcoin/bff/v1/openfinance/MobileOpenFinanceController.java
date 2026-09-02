package com.brunopedraca.celcoin.bff.v1.openfinance;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.openfinance.OpenFinanceDtos.CelcoinOpenFinanceConsentRequest;
import com.brunopedraca.celcoin.openfinance.OpenFinanceDtos.CelcoinOpenFinancePageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/** Curated Open Finance data and consent entry points; no arbitrary upstream path is exposed. */
@RestController
@RequestMapping(path = "/mobile/v1/open-finance", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileOpenFinanceController {
    private final CelcoinClient client;
    public MobileOpenFinanceController(CelcoinClient client) { this.client = client; }
    @GetMapping("/institutions") public Map<String, Object> institutions() { return client.openFinance().listBrands(); }
    @PostMapping(path = "/consents", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> consent(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody ConsentRequest request) { return client.openFinance().createConsent(new CelcoinOpenFinanceConsentRequest(request.brandId(), request.data()), key); }
    @GetMapping("/accounts") public Map<String, Object> accounts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize, @RequestParam(required = false) String paginationKey) { return client.openFinance().accounts(new CelcoinOpenFinancePageRequest(page, pageSize, paginationKey)); }
    @GetMapping("/accounts/{accountId}/transactions") public Map<String, Object> transactions(@PathVariable String accountId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize, @RequestParam(required = false) String paginationKey) { return client.openFinance().accountTransactions(accountId, new CelcoinOpenFinancePageRequest(page, pageSize, paginationKey)); }
    public record ConsentRequest(@NotBlank String brandId, Map<String, Object> data) {}
}
