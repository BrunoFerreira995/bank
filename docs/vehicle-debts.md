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

## Regras de seleção

Antes de pagar, valide `required`, `dependsOn` e `distinct` usando
`CelcoinVehicleDebtSelectionValidator`. Débitos obrigatórios devem estar na
seleção; dependências devem ser incluídas; débitos distintos representam
alternativas e não podem ser enviados juntos.

Os erros oficiais ficam disponíveis em `client.vehicles().errors()`. Erros
`805`, `807` e `130` são transitórios; em `805` ou `813`, faça uma nova
consulta para obter um novo `idConsult` antes de tentar o pagamento.

## Massa de testes

| ID | Cenário | Resultado esperado |
| --- | --- | --- |
| VEH-01 | Consulta com veículo válido | `PROCESSING` e webhook `vehicledebts-consult` |
| VEH-02 | Pagamento de débitos selecionados | `SUCCESS` e webhook `vehicledebts-receipt` |
| VEH-03 | Débito dependente ausente | `815` |
| VEH-04 | Débito obrigatório ausente | `816` |
| VEH-05 | Débitos distintos juntos | `817` |
| VEH-06 | Consulta repetida com mesmo `clientRequestId` | `IVDBE009` |
| VEH-07 | Saldo insuficiente | `803` ou webhook `120` |
| VEH-08 | Detran indisponível | `807` ou webhook `130` |
| VEH-09 | Pagamento repetido com o mesmo `transactionId` | `813` |
| VEH-10 | Débito expirado | `818` |

No sandbox, os cenários são selecionados pela massa oficial da Celcoin e podem
ser exercitados independentemente do estado informado. Consultas e pagamentos
são assíncronos: registre `clientRequestId`, `idConsult`, `debtId`, status,
`errorCode` e payload do webhook para evidência.

## FAQs

**Posso enviar qualquer UF?** Não. Use os estados suportados pela Celcoin; o
sandbox permite simular cenários de todos eles, mas a produção depende da
disponibilidade do Detran.

**Posso consultar a mesma placa várias vezes?** Há limite de uma consulta para
a mesma placa/Renavam em dois minutos. Prefira consultar o status da solicitação.

**O débito baixou imediatamente?** Não necessariamente. A baixa no Detran pode
levar de três a cinco dias úteis; acompanhe o webhook e o status.

**O que fazer após erro 805 ou 813?** Crie uma nova consulta e use o novo
identificador retornado, sem reutilizar a consulta anterior.
