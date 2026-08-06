package com.brunopedraca.celcoin.reconciliation;

import java.util.Map;

public final class CelcoinReconciliationErrors {
    private CelcoinReconciliationErrors() {}

    public record ErrorDescriptor(String code, String description, boolean retryable) {}

    private static final Map<String, ErrorDescriptor> ERRORS = Map.ofEntries(
            Map.entry("001", new ErrorDescriptor("001", "Export file not found", false)),
            Map.entry("002", new ErrorDescriptor("002", "No record in file", false)),
            Map.entry("003", new ErrorDescriptor("003", "Export file not processed", true)),
            Map.entry("004", new ErrorDescriptor("004", "No file type found", false)),
            Map.entry("005", new ErrorDescriptor("005", "Consolidated statement empty", false)),
            Map.entry("991", new ErrorDescriptor("991", "Invalid attribute or file type", false)),
            Map.entry("992", new ErrorDescriptor("992", "User has no permission", false)),
            Map.entry("993", new ErrorDescriptor("993", "Token expired", true)),
            Map.entry("994", new ErrorDescriptor("994", "Missing authorization header", false)),
            Map.entry("995", new ErrorDescriptor("995", "Unauthorized", false)),
            Map.entry("996", new ErrorDescriptor("996", "Non-existent user", false)),
            Map.entry("997", new ErrorDescriptor("997", "User validation failed", true)),
            Map.entry("998", new ErrorDescriptor("998", "Product validation failed", true)),
            Map.entry("999", new ErrorDescriptor("999", "Operation failed", true)));

    public static ErrorDescriptor describe(String code) {
        return ERRORS.getOrDefault(code, new ErrorDescriptor(code, "Unknown reconciliation error", false));
    }
}
