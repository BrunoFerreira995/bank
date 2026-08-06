package com.brunopedraca.celcoin.reconciliation;

import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.reconciliation.CelcoinReconciliationDtos.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class CelcoinReconciliationClient implements CelcoinReconciliationOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinReconciliationClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<Map<String, Object>> listFileTypes() {
        Map<String, Object>[] result = httpClient.get(
                "/tools-conciliation/v1/exportfile/types", Map[].class, context());
        return result == null ? List.of() : List.of(result);
    }

    @Override
    public Map<String, Object> extractFile(ExportFileRequest request) {
        require(request != null && request.fileType() != null, "fileType is required");
        require(request.accountDate() != null, "accountDate is required");
        return httpClient.get("/tools-conciliation/v1/exportfile?" + query(
                        "filetype", request.fileType(), "accountdate", request.accountDate(),
                        "Page", request.page(), "Quantity", request.quantity()),
                Map.class, context());
    }

    @Override
    public List<Map<String, Object>> consolidatedStatement(ConsolidatedStatementRequest request) {
        require(request != null && request.startDate() != null, "startDate is required");
        require(request.endDate() != null, "endDate is required");
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        if (request.startDate().plusDays(15).isBefore(request.endDate())) {
            throw new IllegalArgumentException("statement range must not exceed 15 days");
        }
        Map<String, Object>[] result = httpClient.get(
                "/tools-conciliation/v1/ConsolidatedStatement?" + query(
                        "startDate", request.startDate(), "endDate", request.endDate(),
                        "page", request.page(), "quantity", request.quantity()),
                Map[].class, context());
        return result == null ? List.of() : List.of(result);
    }

    @Override
    public CelcoinReconciliationErrors.ErrorDescriptor error(String code) {
        return CelcoinReconciliationErrors.describe(code);
    }

    private CelcoinRequestContext context() { return CelcoinRequestContext.create(null); }
    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
    private static String query(Object... values) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i += 2) {
            if (values[i + 1] == null) continue;
            if (!result.isEmpty()) result.append('&');
            result.append(values[i]).append('=').append(values[i + 1]);
        }
        return result.toString();
    }
}
