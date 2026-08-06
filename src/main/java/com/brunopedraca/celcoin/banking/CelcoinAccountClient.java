package com.brunopedraca.celcoin.banking;

import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountClosureRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountCountResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountCustomerUpdateRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountFinancialInformationRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountListRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountListResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountMonitoringRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountMonitoringResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountStatusResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountStatusUpdateRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinCoreAccountRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinIncomeReportRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinIncomeReportResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinJudicialBlockRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinJudicialBlockResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinSandboxBalanceRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinTedTransferRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinTedTransferResponse;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import java.util.Map;
import org.springframework.util.StringUtils;

public class CelcoinAccountClient implements CelcoinAccountOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinAccountClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinAccountStatusResponse getStatus(String accountId) {
        ensureConfigured();
        return httpClient.get("/baas/v2/account/check?" + query().param("onboardingId", accountId),
                CelcoinAccountStatusResponse.class, context(null));
    }

    public CelcoinAccountResponse getPersonAccount(String accountId) {
        ensureConfigured();
        return account("/baas/v2/account/fetch?" + query().param("Account", accountId), "PERSON");
    }

    public CelcoinAccountResponse getBusinessAccount(String accountId) {
        ensureConfigured();
        return account("/baas/v2/account/fetch?" + query().param("Account", accountId), "BUSINESS");
    }

    public CelcoinAccountResponse createCoreAccount(CelcoinCoreAccountRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/baas-onboarding/v1/account/" +
                        ("BUSINESS".equalsIgnoreCase(request.type()) ? "business" : "natural-person") + "/create",
                request, CelcoinAccountResponse.class, context(idempotencyKey));
    }

    public CelcoinAccountResponse updateFinancialInformation(CelcoinAccountFinancialInformationRequest request) {
        ensureConfigured();
        return httpClient.put("/baas/v2/account/financial-information", request,
                CelcoinAccountResponse.class, context(null));
    }

    public CelcoinAccountResponse updateCustomer(CelcoinAccountCustomerUpdateRequest request) {
        ensureConfigured();
        return httpClient.put("/baas/v2/account/natural-person?" + query().param("Account", request.accountId()),
                request, CelcoinAccountResponse.class, context(null));
    }

    public CelcoinAccountStatusResponse deactivateAccount(CelcoinAccountClosureRequest request) {
        ensureConfigured();
        return httpClient.put("/baas/v2/account/status?" + query().param("Account", request.accountId()),
                Map.of("status", "BLOQUEADO", "reason", request.reason()),
                CelcoinAccountStatusResponse.class, context(null));
    }

    public CelcoinAccountStatusResponse closeAccount(CelcoinAccountClosureRequest request) {
        ensureConfigured();
        return httpClient.delete("/baas/v2/account/close?" + query().param("Account", request.accountId())
                        .param("Reason", request.reason()), request.metadata(),
                CelcoinAccountStatusResponse.class, context(null));
    }

    public CelcoinAccountListResponse listAccounts(CelcoinAccountListRequest request) {
        ensureConfigured();
        String path = "/baas/v2/account/fetch-all?" + query().param("DocumentNumber", request.document())
                .param("Type", request.type()).param("Status", request.status())
                .param("Page", request.page()).param("Limit", request.size());
        return httpClient.get(path, CelcoinAccountListResponse.class, context(null));
    }

    public CelcoinAccountCountResponse countAccounts(CelcoinAccountListRequest request) {
        ensureConfigured();
        return httpClient.get("/baas/v2/account/fetch-all?" + query().param("DocumentNumber", request.document())
                        .param("Type", request.type()).param("Status", request.status())
                        .param("Page", request.page()).param("Limit", request.size()),
                CelcoinAccountCountResponse.class, context(null));
    }

    public CelcoinJudicialBlockResponse createJudicialBlock(
            CelcoinJudicialBlockRequest request, String idempotencyKey) {
        ensureConfigured();
        return httpClient.post("/baas/v2/account/judicial-block?" + query().param("Account", request.accountId()),
                request, CelcoinJudicialBlockResponse.class, context(idempotencyKey));
    }

    public CelcoinAccountStatusResponse updateStatus(CelcoinAccountStatusUpdateRequest request) {
        ensureConfigured();
        return httpClient.put("/baas/v2/account/status?" + query().param("Account", request.accountId()),
                request, CelcoinAccountStatusResponse.class, context(null));
    }

    public CelcoinBalanceResponse addSandboxBalance(CelcoinSandboxBalanceRequest request) {
        ensureConfigured();
        Map<String, Object> body = Map.of("clientCode", java.util.UUID.randomUUID().toString(),
                "amount", request.amount(), "type", "CREDIT", "description",
                request.description() == null ? "Sandbox credit" : request.description());
        return httpClient.post("/baas/v2/wallet/entry/" + encode(request.accountId()), body,
                CelcoinBalanceResponse.class, context(null));
    }

    public CelcoinAccountMonitoringResponse createMonitoring(CelcoinAccountMonitoringRequest request) {
        ensureConfigured();
        return httpClient.post("/baas/v2/account/monitoring", request,
                CelcoinAccountMonitoringResponse.class, context(null));
    }

    public CelcoinAccountMonitoringResponse simulateMonitoring(String monitoringId, String status) {
        ensureConfigured();
        return httpClient.post("/baas/v2/account/monitoring/" + encode(monitoringId) + "/simulate?"
                        + query().param("status", status), null,
                CelcoinAccountMonitoringResponse.class, context(null));
    }

    public CelcoinBalanceResponse getBalance(String accountId) {
        ensureConfigured();
        return httpClient.get("/baas/v2/wallet/balance?" + query().param("Account", accountId),
                CelcoinBalanceResponse.class, context(null));
    }

    public CelcoinBalanceResponse getDayBalance(String accountId) {
        ensureConfigured();
        return httpClient.get("/baas-walletreports/v1/wallet/dayBalance?" + query().param("Account", accountId),
                CelcoinBalanceResponse.class, context(null));
    }

    public CelcoinStatementResponse getStatement(CelcoinStatementRequest request) {
        ensureConfigured();
        String path = "/baas/v2/wallet/movement?" + query().param("Account", request.accountId())
                .param("DateFrom", request.startDate()).param("DateTo", request.endDate())
                .param("Page", request.page()).param("Limit", request.size());
        return httpClient.get(path, CelcoinStatementResponse.class, context(null));
    }

    @Override
    public CelcoinIncomeReportResponse getIncomeReport(CelcoinIncomeReportRequest request) {
        ensureConfigured();
        String path = "/baas/v2/account/income-report?" + query().param("Account", request.accountId())
                .param("CalendarYear", request.calendarYear()).param("Quarter", request.quarter());
        return httpClient.get(path, CelcoinIncomeReportResponse.class, context(null));
    }

    @Override
    public CelcoinTedTransferResponse transferTed(CelcoinTedTransferRequest request, String idempotencyKey) {
        ensureConfigured();
        String clientCode = StringUtils.hasText(request.clientCode())
                ? request.clientCode()
                : (StringUtils.hasText(idempotencyKey) ? idempotencyKey : java.util.UUID.randomUUID().toString());
        Map<String, Object> body = Map.of(
                "amount", request.amount(),
                "clientCode", clientCode,
                "debitParty", Map.of("account", request.debitAccountId()),
                "creditParty", Map.of(
                        "bank", request.creditParty().bank(),
                        "account", request.creditParty().account(),
                        "branch", request.creditParty().branch(),
                        "taxId", request.creditParty().taxId(),
                        "name", request.creditParty().name(),
                        "accountType", request.creditParty().accountType(),
                        "personType", request.creditParty().personType()),
                "clientFinality", request.clientFinality(),
                "description", request.description() == null ? "" : request.description());
        return httpClient.post("/baas/v2/spb/transfer", body,
                CelcoinTedTransferResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinTedTransferResponse getTedTransferStatus(String id, String clientCode) {
        ensureConfigured();
        String path = "/baas/v2/spb/transfer/status?" + query().param("id", id)
                .param("clientCode", clientCode);
        return httpClient.get(path, CelcoinTedTransferResponse.class, context(null));
    }

    public CelcoinInternalTransferResponse transfer(CelcoinInternalTransferRequest request, String idempotencyKey) {
        ensureConfigured();
        validateInternalTransfer(request);
        String clientRequestId = StringUtils.hasText(request.clientRequestId())
                ? request.clientRequestId()
                : (StringUtils.hasText(idempotencyKey) ? idempotencyKey : java.util.UUID.randomUUID().toString());
        Map<String, Object> creditParty = new java.util.HashMap<>();
        creditParty.put("account", request.targetAccountId());
        if (StringUtils.hasText(request.targetTaxId())) {
            creditParty.put("taxId", request.targetTaxId());
        }
        Map<String, Object> body = Map.of("amount", request.amount(), "clientRequestId", clientRequestId,
                "debitParty", Map.of("account", request.sourceAccountId()),
                "creditParty", creditParty,
                "description", request.description() == null ? "Transfer" : request.description());
        return httpClient.post("/baas/v2/wallet/internal/transfer", body,
                CelcoinInternalTransferResponse.class, context(idempotencyKey));
    }

    public CelcoinInternalTransferResponse getTransferStatus(String transferId) {
        return getTransferStatus(transferId, null, null);
    }

    @Override
    public CelcoinInternalTransferResponse getTransferStatus(
            String transferId, String clientRequestId, String endToEndId) {
        ensureConfigured();
        if (!StringUtils.hasText(transferId) && !StringUtils.hasText(clientRequestId)
                && !StringUtils.hasText(endToEndId)) {
            throw new IllegalArgumentException("transferId, clientRequestId or endToEndId is required");
        }
        String path = "/baas/v2/wallet/internal/transfer/status?" + query().param("Id", transferId)
                .param("ClientRequestId", clientRequestId).param("EndToEndId", endToEndId);
        return httpClient.get(path,
                CelcoinInternalTransferResponse.class, context(null));
    }

    private CelcoinAccountResponse account(String path, String type) {
        CelcoinAccountResponse response = httpClient.get(path, CelcoinAccountResponse.class, context(null));
        return response == null ? null : new CelcoinAccountResponse(response.accountId(), response.document(), type,
                response.status(), response.raw());
    }

    private void ensureConfigured() {
        if (httpClient == null) {
            throw unspecified();
        }
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private QueryBuilder query() { return new QueryBuilder(); }

    private static String encode(String value) {
        return StringUtils.hasText(value) ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8) : "";
    }

    private static void validateInternalTransfer(CelcoinInternalTransferRequest request) {
        if (request == null) throw new IllegalArgumentException("transfer request is required");
        if (!StringUtils.hasText(request.sourceAccountId())) throw new IllegalArgumentException("source account is required");
        if (!StringUtils.hasText(request.targetAccountId())) throw new IllegalArgumentException("target account is required");
        if (request.sourceAccountId().length() > 20 || request.targetAccountId().length() > 20) {
            throw new IllegalArgumentException("account id must have at most 20 characters");
        }
        if (request.sourceAccountId().equals(request.targetAccountId())) {
            throw new IllegalArgumentException("source and target accounts must be different");
        }
        if (request.amount() == null || request.amount().signum() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (StringUtils.hasText(request.clientRequestId()) && request.clientRequestId().length() > 200) {
            throw new IllegalArgumentException("clientRequestId must have at most 200 characters");
        }
        if (request.description() != null && request.description().length() > 200) {
            throw new IllegalArgumentException("description must have at most 200 characters");
        }
    }

    private static final class QueryBuilder {
        private final StringBuilder value = new StringBuilder();
        QueryBuilder param(String name, Object param) {
            if (param != null && StringUtils.hasText(String.valueOf(param))) {
                if (!value.isEmpty()) value.append('&');
                value.append(name).append('=').append(encode(String.valueOf(param)));
            }
            return this;
        }
        @Override public String toString() { return value.toString(); }
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin account endpoint path is not configured because the official contract was not provided in this first version");
    }
}
