package com.brunopedraca.celcoin.reconciliation;

import java.time.LocalDate;

public final class CelcoinReconciliationDtos {
    private CelcoinReconciliationDtos() {}

    public record ExportFileRequest(Integer fileType, LocalDate accountDate, Integer page, Integer quantity) {}

    public record ConsolidatedStatementRequest(
            LocalDate startDate, LocalDate endDate, Integer page, Integer quantity) {}
}
