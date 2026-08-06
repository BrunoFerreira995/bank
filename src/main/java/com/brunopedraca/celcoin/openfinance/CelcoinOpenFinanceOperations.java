package com.brunopedraca.celcoin.openfinance;

import com.brunopedraca.celcoin.openfinance.OpenFinanceDtos.*;
import java.util.Map;

public interface CelcoinOpenFinanceOperations {
    Map<String, Object> listBrands();

    Map<String, Object> getBrand(String brandId);

    Map<String, Object> createConsent(CelcoinOpenFinanceConsentRequest request, String idempotencyKey);

    Map<String, Object> processCallback(CelcoinOpenFinanceCallbackRequest request);

    Map<String, Object> listResources(CelcoinOpenFinancePageRequest request);

    Map<String, Object> getData(String resourcePath, CelcoinOpenFinancePageRequest request);

    Map<String, Object> personalIdentifications(CelcoinOpenFinancePageRequest request);

    Map<String, Object> personalQualifications(CelcoinOpenFinancePageRequest request);

    Map<String, Object> personalFinancialRelations(CelcoinOpenFinancePageRequest request);

    Map<String, Object> businessIdentifications(CelcoinOpenFinancePageRequest request);

    Map<String, Object> businessQualifications(CelcoinOpenFinancePageRequest request);

    Map<String, Object> businessFinancialRelations(CelcoinOpenFinancePageRequest request);

    Map<String, Object> accounts(CelcoinOpenFinancePageRequest request);

    Map<String, Object> accountTransactions(String accountId, CelcoinOpenFinancePageRequest request);

    Map<String, Object> loans(CelcoinOpenFinancePageRequest request);

    Map<String, Object> financings(CelcoinOpenFinancePageRequest request);

    Map<String, Object> creditCardAccounts(CelcoinOpenFinancePageRequest request);

    Map<String, Object> bankFixedIncome(CelcoinOpenFinancePageRequest request);

    Map<String, Object> creditFixedIncome(CelcoinOpenFinancePageRequest request);

    Map<String, Object> variableIncome(CelcoinOpenFinancePageRequest request);

    Map<String, Object> treasury(CelcoinOpenFinancePageRequest request);

    Map<String, Object> funds(CelcoinOpenFinancePageRequest request);
}
