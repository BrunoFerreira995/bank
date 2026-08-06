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

public interface CelcoinAccountOperations {
    CelcoinAccountStatusResponse getStatus(String accountId);

    CelcoinAccountResponse getPersonAccount(String accountId);

    CelcoinAccountResponse getBusinessAccount(String accountId);

    default CelcoinAccountResponse createCoreAccount(CelcoinCoreAccountRequest request) {
        return createCoreAccount(request, null);
    }

    CelcoinAccountResponse createCoreAccount(CelcoinCoreAccountRequest request, String idempotencyKey);

    CelcoinAccountResponse updateFinancialInformation(CelcoinAccountFinancialInformationRequest request);

    CelcoinAccountResponse updateCustomer(CelcoinAccountCustomerUpdateRequest request);

    CelcoinAccountStatusResponse deactivateAccount(CelcoinAccountClosureRequest request);

    CelcoinAccountStatusResponse closeAccount(CelcoinAccountClosureRequest request);

    CelcoinAccountListResponse listAccounts(CelcoinAccountListRequest request);

    CelcoinAccountCountResponse countAccounts(CelcoinAccountListRequest request);

    default CelcoinJudicialBlockResponse createJudicialBlock(CelcoinJudicialBlockRequest request) {
        return createJudicialBlock(request, null);
    }

    CelcoinJudicialBlockResponse createJudicialBlock(CelcoinJudicialBlockRequest request, String idempotencyKey);

    CelcoinAccountStatusResponse updateStatus(CelcoinAccountStatusUpdateRequest request);

    CelcoinBalanceResponse addSandboxBalance(CelcoinSandboxBalanceRequest request);

    CelcoinAccountMonitoringResponse createMonitoring(CelcoinAccountMonitoringRequest request);

    CelcoinAccountMonitoringResponse simulateMonitoring(String monitoringId, String status);

    CelcoinBalanceResponse getBalance(String accountId);

    CelcoinBalanceResponse getDayBalance(String accountId);

    CelcoinStatementResponse getStatement(CelcoinStatementRequest request);

    CelcoinIncomeReportResponse getIncomeReport(CelcoinIncomeReportRequest request);

    default CelcoinTedTransferResponse transferTed(CelcoinTedTransferRequest request) {
        return transferTed(request, null);
    }

    CelcoinTedTransferResponse transferTed(CelcoinTedTransferRequest request, String idempotencyKey);

    default CelcoinTedTransferResponse getTedTransferStatus(String id) {
        return getTedTransferStatus(id, null);
    }

    CelcoinTedTransferResponse getTedTransferStatus(String id, String clientCode);

    default CelcoinIncomeReportResponse getIncomeReport(String accountId, Integer calendarYear) {
        return getIncomeReport(new CelcoinIncomeReportRequest(accountId, calendarYear));
    }

    default CelcoinIncomeReportResponse getIncomeReportPf(String accountId, Integer calendarYear) {
        return getIncomeReport(accountId, calendarYear);
    }

    default CelcoinIncomeReportResponse getIncomeReportPj(
            String accountId, Integer calendarYear, Integer quarter) {
        return getIncomeReport(new CelcoinIncomeReportRequest(accountId, calendarYear, quarter));
    }

    default CelcoinInternalTransferResponse transfer(CelcoinInternalTransferRequest request) {
        return transfer(request, null);
    }

    CelcoinInternalTransferResponse transfer(CelcoinInternalTransferRequest request, String idempotencyKey);

    CelcoinInternalTransferResponse getTransferStatus(String transferId);

    default CelcoinInternalTransferResponse getTransferStatus(String transferId, String clientRequestId) {
        return getTransferStatus(transferId, clientRequestId, null);
    }

    CelcoinInternalTransferResponse getTransferStatus(
            String transferId, String clientRequestId, String endToEndId);
}
