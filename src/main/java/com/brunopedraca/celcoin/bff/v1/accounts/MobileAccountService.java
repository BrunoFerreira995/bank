package com.brunopedraca.celcoin.bff.v1.accounts;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinIncomeReportRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountClosureRequest;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class MobileAccountService {
    private final CelcoinClient celcoinClient;

    public MobileAccountService(CelcoinClient celcoinClient) {
        this.celcoinClient = celcoinClient;
    }

    public AccountBalanceResponse balance(String accountId) {
        CelcoinBalanceResponse balance = celcoinClient.accounts().getBalance(accountId);
        return new AccountBalanceResponse(
                balance.accountId(), balance.available(), balance.blocked(), balance.currency());
    }
    public AccountResponse account(String accountId) {
        CelcoinAccountResponse account = celcoinClient.accounts().getPersonAccount(accountId);
        return new AccountResponse(account.accountId(), account.document(), account.type(), account.status());
    }
    public StatementResponse statement(String accountId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        CelcoinStatementResponse result = celcoinClient.accounts().getStatement(new CelcoinStatementRequest(accountId, startDate, endDate, page, size));
        java.util.List<TransactionResponse> transactions = result.transactions() == null ? java.util.List.of() : result.transactions().stream()
                .map(item -> new TransactionResponse(item.transactionId(), item.createdAt(), item.amount(), item.type(), item.status())).toList();
        return new StatementResponse(result.accountId(), transactions, result.page(), result.size(), result.total(), result.hasNext());
    }
    public IncomeReportResponse incomeReport(String accountId, Integer year, Integer quarter) {
        var value = celcoinClient.accounts().getIncomeReport(new CelcoinIncomeReportRequest(accountId, year, quarter));
        var body = value.body();
        return new IncomeReportResponse(value.status(), body == null ? null : body.incomeFile(), body == null ? null : body.fileType());
    }
    public AccountStatusResponse close(String accountId, String reason) {
        var value = celcoinClient.accounts().closeAccount(new CelcoinAccountClosureRequest(accountId, reason, null));
        return new AccountStatusResponse(value.accountId(), value.status());
    }
}
