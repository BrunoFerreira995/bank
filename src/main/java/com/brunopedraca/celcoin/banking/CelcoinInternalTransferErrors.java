package com.brunopedraca.celcoin.banking;

import java.util.List;

/** Official CBE errors for internal BaaS transfers and status queries. */
public final class CelcoinInternalTransferErrors {
    private CelcoinInternalTransferErrors() {}

    public record Error(String code, String message, boolean retryable, String action) {}

    public static List<Error> all() {
        return List.of(
                new Error("CBE094", "amount is required", false, "Send a positive amount"),
                new Error("CBE095", "amount is invalid or not greater than zero", false, "Correct amount"),
                new Error("CBE100", "Identical pending entry exists", true, "Wait and query status"),
                new Error("CBE101", "clientRequestId already exists", false, "Use a new id or query the existing transfer"),
                new Error("CBE102", "Amount exceeds the operation limit", false, "Use an amount within the limit"),
                new Error("CBE107", "debitParty is required", false, "Send source account"),
                new Error("CBE108", "debitParty.account is required", false, "Send source account"),
                new Error("CBE109", "debitParty.account is invalid", false, "Correct source account"),
                new Error("CBE110", "debitParty.account exceeds 20 characters", false, "Correct source account"),
                new Error("CBE115", "creditParty is required", false, "Send target account"),
                new Error("CBE116", "creditParty.account is required", false, "Send target account"),
                new Error("CBE117", "creditParty.account is invalid", false, "Correct target account"),
                new Error("CBE118", "creditParty.account exceeds 20 characters", false, "Correct target account"),
                new Error("CBE123", "Insufficient balance", false, "Fund the source account"),
                new Error("CBE124", "Source account is closed", false, "Use an active source account"),
                new Error("CBE125", "Target account is closed", false, "Use an active target account"),
                new Error("CBE147", "Source account is blocked", false, "Unblock the source account"),
                new Error("CBE148", "Target account is blocked", false, "Unblock the target account"),
                new Error("CBE261", "clientRequestId is required", false, "Send clientRequestId"),
                new Error("CBE308", "clientRequestId exceeds 200 characters", false, "Use a shorter id"),
                new Error("CBE312", "Transfer to the same account is not allowed", false, "Use a different target"),
                new Error("CBE314", "Source account not found", false, "Check source account"),
                new Error("CBE315", "Target account not found", false, "Check target account"),
                new Error("CBE328", "Credit account client is inactive", false, "Activate the credit account client"),
                new Error("CBE329", "At least id or clientRequestId is required", false, "Send a query identifier"),
                new Error("CBE332", "Id exceeds 36 characters", false, "Use a valid transfer id"),
                new Error("CBE666", "Account has MED block", false, "Resolve the MED restriction"));
    }

    public static Error find(String code) {
        return all().stream().filter(error -> error.code().equalsIgnoreCase(code)).findFirst()
                .orElse(new Error(code, "Unmapped internal transfer error", false, "Consult Celcoin response"));
    }
}
