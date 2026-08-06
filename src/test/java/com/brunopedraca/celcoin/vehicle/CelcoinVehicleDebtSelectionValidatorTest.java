package com.brunopedraca.celcoin.vehicle;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebt;
import java.util.List;
import org.junit.jupiter.api.Test;

class CelcoinVehicleDebtSelectionValidatorTest {
    @Test
    void enforcesRequiredDependenciesAndDistinctAlternatives() {
        var required = debt("required", true, List.of(), List.of());
        var dependent = debt("dependent", false, List.of("required"), List.of());
        var alternative = debt("alternative", false, List.of(), List.of("other"));
        var other = debt("other", false, List.of(), List.of("alternative"));

        assertThat(CelcoinVehicleDebtSelectionValidator.validate(
                List.of(required, dependent, alternative, other), List.of("dependent")).valid()).isFalse();
        assertThat(CelcoinVehicleDebtSelectionValidator.validate(
                List.of(required, dependent, alternative, other), List.of("required", "dependent", "alternative", "other")).reason())
                .contains("distinct");
        assertThat(CelcoinVehicleDebtSelectionValidator.validate(
                List.of(required, dependent, alternative, other), List.of("required", "dependent")).valid()).isTrue();
    }

    @Test
    void exposesVehicleErrorCatalog() {
        assertThat(CelcoinVehicleErrors.find("815").message()).contains("Dependent");
        assertThat(CelcoinVehicleErrors.find("807").retryable()).isTrue();
        assertThat(CelcoinVehicleErrors.find("818").retryable()).isFalse();
    }

    private static CelcoinVehicleDebt debt(String id, boolean required, List<String> dependsOn, List<String> distinct) {
        return new CelcoinVehicleDebt(id, id, null, id, null, null, null, null, false, false,
                "ticket", null, required, dependsOn, distinct, null);
    }
}
