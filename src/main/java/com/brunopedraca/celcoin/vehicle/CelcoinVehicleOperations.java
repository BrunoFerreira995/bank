package com.brunopedraca.celcoin.vehicle;

import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtConsultRequest;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtConsultResponse;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtPaymentRequest;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtResponse;

public interface CelcoinVehicleOperations {
    CelcoinVehicleDebtConsultResponse consult(CelcoinVehicleDebtConsultRequest request);

    CelcoinVehicleDebtConsultResponse getConsultation(String idConsult, String clientRequestId, String debtId);

    default CelcoinVehicleDebtResponse pay(CelcoinVehicleDebtPaymentRequest request) {
        return pay(request, null);
    }

    CelcoinVehicleDebtResponse pay(CelcoinVehicleDebtPaymentRequest request, String idempotencyKey);

    CelcoinVehicleDebtResponse getPaymentStatus(String idConsult, String clientRequestId, String debtId);
}
