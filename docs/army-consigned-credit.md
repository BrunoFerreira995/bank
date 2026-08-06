# Empréstimo Consignado — Servidores do Exército

O fluxo é atendido pelo cliente `celcoinClient.credit()` e usa as mesmas
credenciais OAuth2 da Plataforma de Crédito. As credenciais, massa de testes,
produto, funding e workflow são liberados pelo time de implantação.

## Jornada de API

```java
var token = celcoinClient.credit().authenticateArmyConsigned();
var margin = celcoinClient.credit()
        .getArmyConsignedMargin(productId, taxpayerId);
var simulation = celcoinClient.credit()
        .simulateArmyConsignedCcb(productId, simulationRequest);
var borrower = celcoinClient.credit()
        .createArmyConsignedBorrower(personRequest);
```

Depois do aceite da simulação, a CCB é criada pelo fluxo padrão de
`createApplication`. A operação passa por KYC, assinatura, reserva de margem,
averbação e desembolso.

## Compra com troco

O request de `createArmyConsignedPurchaseBundle` deve conter `applications`,
`workflow_definition`, `balance_check_id`, `simulation_id`, os tokens do
consignado e as informações de liquidação dos contratos adquiridos.

```java
var bundle = celcoinClient.credit()
        .createArmyConsignedPurchaseBundle(bundleRequest);
var status = celcoinClient.credit()
        .getArmyConsignedPurchaseBundle(String.valueOf(bundle.get("id")));
```

O bundle é assíncrono. O status pode ser `PENDING`, `COMPLETED` ou `CANCELED`,
e o andamento das CCBs deve ser acompanhado pelo webhook cadastrado.

## Status

Use `getArmyConsignedOperationStatus(applicationId)` para consultar estados
como `KYC_PROCESSING`, `PENDING_SIGNATURE`, `PENDING_GUARANTEE`,
`PENDING_DISBURSEMENT`, `ISSUED` e `CANCELED`.

O endpoint e a operação dependem de produto contratado, convênio e massa de
homologação habilitada; não use CPF de produção no sandbox.
