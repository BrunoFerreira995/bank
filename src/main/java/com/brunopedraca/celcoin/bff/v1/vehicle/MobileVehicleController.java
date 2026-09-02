package com.brunopedraca.celcoin.bff.v1.vehicle;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtConsultRequest;
import com.brunopedraca.celcoin.vehicle.VehicleDtos.CelcoinVehicleDebtPaymentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/mobile/v1/vehicles/debts", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileVehicleController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobileVehicleController(CelcoinClient client, MobileAccountAuthorizationService authorization) { this.client = client; this.authorization = authorization; }
    @PostMapping(path = "/consultations", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object consult(@Valid @RequestBody ConsultRequest request) { authorization.requireRead(request.accountId()); return client.vehicles().consult(new CelcoinVehicleDebtConsultRequest(request.state(), request.licensePlate(), request.renavam(), request.documentNumber(), request.clientRequestId())); }
    @PostMapping(path = "/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object pay(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody PaymentRequest request) { authorization.requireRisk(request.accountId()); return client.vehicles().pay(new CelcoinVehicleDebtPaymentRequest(request.accountId(), request.clientRequestId(), request.consultationId(), request.debtIds(), null), key); }
    public record ConsultRequest(@NotBlank String accountId, @NotBlank String state, @NotBlank String licensePlate, @NotBlank String renavam, @NotBlank String documentNumber, @NotBlank String clientRequestId) {}
    public record PaymentRequest(@NotBlank String accountId, @NotBlank String clientRequestId, @NotBlank String consultationId, List<@NotBlank String> debtIds) {}
}
