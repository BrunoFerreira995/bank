# Portabilidade de Crédito

O `CelcoinCreditOperations` expõe o fluxo do Crédito do Trabalhador:

1. `simulatePortability` simula taxa, parcela e saldo da nova operação.
2. `requestPortabilityAuthorization` envia o termo de consentimento.
3. `authorizePortabilityEmploymentQuery` obtém autorização para consulta.
4. `getPortabilityEmploymentBalance` consulta margem, vínculo e contratos elegíveis.
5. `createPortabilityBundle` solicita a emissão da CCB de portabilidade.
6. `getPortabilityBundle` acompanha o workflow e seus webhooks.

```java
var simulation = celcoin.credit().simulatePortability(productId, Map.of(
        "requested_amount", 1500,
        "interest_rate", 0.0019,
        "num_payments", 3,
        "first_payment_date", "2026-12-28",
        "disbursement_date", "2026-11-25",
        "borrower_type", "PERSON",
        "schedule_type", "MONTHLY"));

var balance = celcoin.credit().getPortabilityEmploymentBalance(productId, taxpayerId);
var bundle = celcoin.credit().createPortabilityBundle(bundleRequest);
```

O `bundleRequest` deve conter `workflow_definition`, `applications`,
`simulation_id`, `balance_check_id`, `contracts_to_purchase` e as variáveis do
contrato original, incluindo ISPB, saldo, taxa, parcelas e valor da parcela.

A averbação e o envio do contrato à Dataprev ocorrem após a quitação do saldo e
a confirmação da instituição original; o contrato público consultado não expõe
uma chamada adicional para forçar essa etapa.
