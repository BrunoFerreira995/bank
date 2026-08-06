package com.brunopedraca.celcoin.topup;

import java.util.List;

public final class CelcoinTopupErrors {
    private CelcoinTopupErrors() {}
    public record Error(String code, String message, boolean retryable, String action) {}
    public static List<Error> all() {
        return List.of(
                new Error("ITBE001", "TransactionId or clientRequestId is required", false, "Send an identifier"),
                new Error("ITBE002", "Transaction not found", false, "Check the transaction"),
                new Error("ITBE003", "clientRequestId is required", false, "Send a unique id"),
                new Error("ITBE005", "providerId is required", false, "Use a provider from the catalog"),
                new Error("ITBE006", "topupData.countryCode is required", false, "Send country code"),
                new Error("ITBE007", "topupData.stateCode is required", false, "Send area/state code"),
                new Error("ITBE008", "topupData.number is required", false, "Send target number"),
                new Error("ITBE011", "clientRequestId already exists", false, "Query the existing topup"),
                new Error("ITBE017", "Account exceeds 20 characters", false, "Correct account"),
                new Error("ITBE018", "clientRequestId exceeds 200 characters", false, "Use a shorter id"),
                new Error("ITBE019", "Duplicate tag keys", false, "Use unique tag keys"),
                new Error("ITBE020", "Account not found or inactive", false, "Use an active account"),
                new Error("ITBE024", "Invalid target number", false, "Correct topupData.number"),
                new Error("ITBE025", "Maximum of 20 tags", false, "Remove tags"),
                new Error("ITBE045", "Invalid topup amount", false, "Use a value returned by provider-values"),
                new Error("ITBE028", "TransactionId or clientRequestId is required for query", false, "Send an identifier"),
                new Error("ITBE029", "Topup not found", false, "Check the transaction"));
    }
    public static Error find(String code) {
        return all().stream().filter(error -> error.code().equalsIgnoreCase(code)).findFirst()
                .orElse(new Error(code, "Unmapped topup error", false, "Consult Celcoin response"));
    }
}
