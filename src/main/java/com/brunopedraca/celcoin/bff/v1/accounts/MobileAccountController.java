package com.brunopedraca.celcoin.bff.v1.accounts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile/v1/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
@Tag(name = "Mobile v1 - Accounts")
public class MobileAccountController {
    private final MobileAccountService service;
    private final MobileAccountAuthorizationService authorization;

    public MobileAccountController(MobileAccountService service, MobileAccountAuthorizationService authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get account balance for the mobile app")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Balance returned"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "502", description = "Celcoin unavailable")
    })
    public AccountBalanceResponse balance(@PathVariable String accountId) {
        authorization.requireRead(accountId);
        return service.balance(accountId);
    }

    @GetMapping
    @Operation(summary = "List accounts linked to the authenticated user")
    public java.util.List<AccountResponse> accounts() {
        return authorization.ownedAccountIds().stream().map(service::account).toList();
    }
    @GetMapping("/active")
    public ActiveAccountResponse active() { return new ActiveAccountResponse(authorization.activeAccountId()); }
    @PutMapping("/active")
    public ActiveAccountResponse selectActive(@RequestBody ActiveAccountRequest request) {
        authorization.selectActiveAccount(request.accountId());
        return new ActiveAccountResponse(request.accountId());
    }

    @GetMapping("/{accountId}/movements/today")
    @Operation(summary = "Get current-day account movements")
    public StatementResponse movementsToday(@PathVariable String accountId) {
        authorization.requireRead(accountId);
        return service.statement(accountId, java.time.LocalDate.now(), java.time.LocalDate.now(), 0, 100);
    }

    @GetMapping("/{accountId}/statement")
    @Operation(summary = "Get a paginated account statement")
    public StatementResponse statement(@PathVariable String accountId,
            @RequestParam java.time.LocalDate startDate, @RequestParam java.time.LocalDate endDate,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "50") Integer size) {
        authorization.requireRead(accountId);
        return service.statement(accountId, startDate, endDate, page, Math.min(size, 100));
    }

    @GetMapping("/{accountId}/income-report")
    public IncomeReportResponse incomeReport(@PathVariable String accountId, @RequestParam Integer year,
            @RequestParam(required = false) Integer quarter) {
        authorization.requireRead(accountId);
        return service.incomeReport(accountId, year, quarter);
    }

    @PostMapping(path = "/{accountId}/close", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountStatusResponse close(@PathVariable String accountId, @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody(required = false) CloseAccountRequest request) {
        authorization.requireRisk(accountId);
        return service.close(accountId, request == null ? null : request.reason());
    }
    public record CloseAccountRequest(String reason) {}
    public record ActiveAccountRequest(@jakarta.validation.constraints.NotBlank String accountId) {}
    public record ActiveAccountResponse(String accountId) {}
}
