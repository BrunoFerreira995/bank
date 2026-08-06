package com.brunopedraca.celcoin.reconciliation;

import com.brunopedraca.celcoin.reconciliation.CelcoinReconciliationDtos.*;
import java.util.List;
import java.util.Map;

public interface CelcoinReconciliationOperations {
    List<Map<String, Object>> listFileTypes();

    Map<String, Object> extractFile(ExportFileRequest request);

    List<Map<String, Object>> consolidatedStatement(ConsolidatedStatementRequest request);

    CelcoinReconciliationErrors.ErrorDescriptor error(String code);
}
