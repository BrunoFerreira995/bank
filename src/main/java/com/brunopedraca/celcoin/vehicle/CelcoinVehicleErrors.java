package com.brunopedraca.celcoin.vehicle;

import java.util.List;

/** Synchronous and asynchronous error catalog for vehicle debt operations. */
public final class CelcoinVehicleErrors {
    private CelcoinVehicleErrors() {}
    public record Error(String code, String message, boolean retryable, String action) {}

    public static List<Error> all() {
        return List.of(
                new Error("IVDBE001", "License plate is required", false, "Send licensePlate"),
                new Error("IVDBE002", "clientRequestId is required", false, "Send a unique id"),
                new Error("IVDBE003", "Renavam is required", false, "Send renavam"),
                new Error("IVDBE004", "Document is required", false, "Send documentNumber"),
                new Error("IVDBE005", "clientRequestId exceeds 200 characters", false, "Use a shorter id"),
                new Error("IVDBE006", "State is required", false, "Send a supported UF"),
                new Error("IVDBE007", "Invalid state", false, "Use a supported UF"),
                new Error("IVDBE008", "Invalid document", false, "Correct the CPF/CNPJ"),
                new Error("IVDBE009", "clientRequestId already used", false, "Create a new consultation"),
                new Error("IVDBE010", "Invalid plate or Renavam", false, "Correct vehicle data"),
                new Error("IVDBE011", "State unavailable", true, "Try a supported state later"),
                new Error("IVDIE999", "Consultation failed", true, "Retry after checking status"),
                new Error("811", "Consultation identifier not found", false, "Use a fresh consultation"),
                new Error("803", "Insufficient balance", false, "Fund the account"),
                new Error("805", "Debt settlement unavailable", true, "Consult again and pay with the new id"),
                new Error("807", "Detran unavailable", true, "Try again later"),
                new Error("813", "Payment already made with this consultation", false, "Consult again"),
                new Error("815", "Dependent debts must be paid together", false, "Include dependencies"),
                new Error("816", "Required debts must be paid", false, "Include required debts"),
                new Error("817", "Distinct debts cannot be paid together", false, "Select one alternative"),
                new Error("818", "Debt is expired", false, "Consult the vehicle again"),
                new Error("822", "clientRequestId already used", false, "Use a new payment id"),
                new Error("833", "No debts found for this payment", false, "Select at least one debt"),
                new Error("834", "Duplicate debt identifier", false, "Remove duplicate ids"),
                new Error("100", "Payment settlement is already open", false, "Do not duplicate payment"),
                new Error("110", "A dependent debt has an open settlement", false, "Wait for settlement"),
                new Error("120", "Insufficient balance for settlement", false, "Fund the account"),
                new Error("130", "Detran unavailable for payment", true, "Retry later"),
                new Error("900", "Transaction not authorised", false, "Review the payment result"));
    }

    public static Error find(String code) {
        return all().stream().filter(error -> error.code().equalsIgnoreCase(code)).findFirst()
                .orElse(new Error(code, "Unmapped vehicle debt error", false, "Consult Celcoin response"));
    }
}
