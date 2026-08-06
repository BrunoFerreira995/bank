package com.brunopedraca.celcoin.boleto;

import java.util.List;

/** Official PCE errors returned by the Celcoin bill-payment endpoint. */
public final class CelcoinBoletoPaymentErrors {
    private CelcoinBoletoPaymentErrors() {}

    public record Error(String code, String message, boolean retryable, String action) {}

    public static List<Error> all() {
        return List.of(
                new Error("PCE009", "transactionIdAuthorize is required", false, "Authorize the barcode first"),
                new Error("PCE010", "Account not found", false, "Check the debit account"),
                new Error("PCE011", "clientRequestId cannot exceed 20 characters", false, "Use a shorter id"),
                new Error("PCE012", "Invalid amount", false, "Use the authorized amount or allowed range"),
                new Error("PCE013", "amount is required", false, "Send amount"),
                new Error("PCE014", "amount must be at least 0.01", false, "Send a positive amount"),
                new Error("PCE015", "Client is not active for this API", false, "Enable the product with Celcoin"),
                new Error("PCE016", "clientRequestId or id is required", false, "Send a request identifier"),
                new Error("PCE018", "Transaction not found", false, "Authorize or query the correct transaction"),
                new Error("PCE019", "Client is not authorized for this product", false, "Check product permissions"),
                new Error("PCE024", "Request format is invalid", false, "Review the request schema"),
                new Error("PCE025", "Payment already exists for clientRequestId", false, "Query payment status; do not duplicate"),
                new Error("PCE026", "Payment already exists for transactionIdAuthorize", false, "Query the existing payment"),
                new Error("PCE040", "Amount exceeds the maximum allowed", false, "Use the authorized maximum"),
                new Error("PCE050", "Bill does not allow amount changes", false, "Use totalUpdated from authorization"),
                new Error("PCE092", "Payment failed; try again", true, "Query status before retrying"));
    }

    public static Error find(String code) {
        return all().stream().filter(error -> error.code().equalsIgnoreCase(code)).findFirst()
                .orElse(new Error(code, "Unmapped bill-payment error", false, "Consult the Celcoin response"));
    }
}
