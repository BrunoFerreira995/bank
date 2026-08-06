# Débito Veicular

O módulo `vehicles()` permite consultar débitos de um veículo, efetivar o
pagamento dos débitos selecionados e consultar o status da operação.

```java
var consultation = celcoin.vehicles().consult(
        new VehicleDtos.CelcoinVehicleDebtConsultRequest(
                "SP", "ABC1D23", "12345678901", "12345678901", "consulta-001"));

var payment = celcoin.vehicles().pay(
        new VehicleDtos.CelcoinVehicleDebtPaymentRequest(
                "30023646056263", "pagamento-001", "consulta-001",
                List.of("debt-id"), List.of()));
```

As consultas são assíncronas; os débitos completos podem chegar pelo webhook
correspondente. `dependsOn`, `distinct` e `required` são preservados no DTO para
que a aplicação valide as regras de combinação antes do pagamento.
