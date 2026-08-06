package com.brunopedraca.celcoin.vehicle;

import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtConsultRequest;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtConsultResponse;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtPaymentRequest;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StringUtils;

public class CelcoinVehicleClient implements CelcoinVehicleOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinVehicleClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CelcoinVehicleDebtConsultResponse consult(CelcoinVehicleDebtConsultRequest request) {
        ensureConfigured();
        validateConsult(request);
        return httpClient.post("/baas/v2/vehicledebts/consult", request,
                CelcoinVehicleDebtConsultResponse.class, context(request.clientRequestId()));
    }

    @Override
    public CelcoinVehicleDebtConsultResponse getConsultation(
            String idConsult, String clientRequestId, String debtId) {
        ensureConfigured();
        return httpClient.get("/baas/v2/vehicledebts/consult?"
                        + query("IdConsult", idConsult, "ClientRequestId", clientRequestId, "DebitId", debtId),
                CelcoinVehicleDebtConsultResponse.class, context(null));
    }

    @Override
    public CelcoinVehicleDebtResponse pay(CelcoinVehicleDebtPaymentRequest request, String idempotencyKey) {
        ensureConfigured();
        validatePayment(request);
        return httpClient.post("/baas/v2/vehicledebts", request,
                CelcoinVehicleDebtResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinVehicleDebtResponse getPaymentStatus(
            String idConsult, String clientRequestId, String debtId) {
        ensureConfigured();
        return httpClient.get("/baas/v2/vehicledebts?"
                        + query("IdConsult", idConsult, "ClientRequestId", clientRequestId, "DebitId", debtId),
                CelcoinVehicleDebtResponse.class, context(null));
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private static String query(String... values) {
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < values.length; i += 2) {
            if (!StringUtils.hasText(values[i + 1])) {
                continue;
            }
            if (!query.isEmpty()) {
                query.append('&');
            }
            query.append(values[i]).append('=')
                    .append(URLEncoder.encode(values[i + 1], StandardCharsets.UTF_8));
        }
        return query.toString();
    }

    private void ensureConfigured() {
        if (httpClient == null) {
            throw new IllegalStateException("Celcoin vehicle endpoint is not configured");
        }
    }

    private static void validateConsult(CelcoinVehicleDebtConsultRequest request) {
        if (request == null) throw new IllegalArgumentException("vehicle consultation is required");
        if (!StringUtils.hasText(request.state()) || request.state().length() != 2) {
            throw new IllegalArgumentException("state must be a two-letter UF");
        }
        if (!StringUtils.hasText(request.licensePlate()) || request.licensePlate().length() > 15) {
            throw new IllegalArgumentException("licensePlate is required and must have at most 15 characters");
        }
        if (!StringUtils.hasText(request.renavam()) || request.renavam().length() > 20) {
            throw new IllegalArgumentException("renavam is required and must have at most 20 characters");
        }
        if (!StringUtils.hasText(request.documentNumber())) throw new IllegalArgumentException("documentNumber is required");
        if (!StringUtils.hasText(request.clientRequestId()) || request.clientRequestId().length() > 200) {
            throw new IllegalArgumentException("clientRequestId is required and must have at most 200 characters");
        }
    }

    private static void validatePayment(CelcoinVehicleDebtPaymentRequest request) {
        if (request == null) throw new IllegalArgumentException("vehicle payment is required");
        if (!StringUtils.hasText(request.account())) throw new IllegalArgumentException("account is required");
        if (!StringUtils.hasText(request.clientRequestId()) || request.clientRequestId().length() > 200) {
            throw new IllegalArgumentException("clientRequestId is required and must have at most 200 characters");
        }
        if (!StringUtils.hasText(request.idConsult())) throw new IllegalArgumentException("idConsult is required");
        var result = CelcoinVehicleDebtSelectionValidator.validate(null, request.debtIdList());
        if (!result.valid() && !"consultation has no debts".equals(result.reason())) {
            throw new IllegalArgumentException(result.reason());
        }
    }
}
