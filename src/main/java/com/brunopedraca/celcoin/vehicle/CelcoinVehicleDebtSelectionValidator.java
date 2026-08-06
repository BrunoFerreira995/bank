package com.brunopedraca.celcoin.vehicle;

import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebt;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Validates required, dependent and distinct debt rules before payment. */
public final class CelcoinVehicleDebtSelectionValidator {
    private CelcoinVehicleDebtSelectionValidator() {}
    public record Result(boolean valid, String reason) {}

    public static Result validate(List<CelcoinVehicleDebt> debts, List<String> selectedIds) {
        if (selectedIds == null || selectedIds.isEmpty()) return new Result(false, "at least one debt is required");
        Set<String> selected = new HashSet<>(selectedIds);
        if (selected.size() != selectedIds.size()) return new Result(false, "duplicate debt identifier");
        if (debts == null || debts.isEmpty()) return new Result(false, "consultation has no debts");
        for (CelcoinVehicleDebt debt : debts) {
            String id = id(debt);
            if (Boolean.TRUE.equals(debt.required()) && !selected.contains(id)) {
                return new Result(false, "required debt must be selected: " + id);
            }
            if (selected.contains(id) && debt.dependsOn() != null
                    && !selected.containsAll(debt.dependsOn())) {
                return new Result(false, "dependent debts must be selected with: " + id);
            }
            if (selected.contains(id) && debt.distinct() != null
                    && debt.distinct().stream().anyMatch(selected::contains)) {
                return new Result(false, "distinct debts cannot be selected together: " + id);
            }
        }
        return new Result(true, null);
    }

    private static String id(CelcoinVehicleDebt debt) {
        return debt.debtId() != null ? debt.debtId() : debt.id();
    }
}
