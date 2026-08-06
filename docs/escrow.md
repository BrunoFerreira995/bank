# Conta Escrow

O módulo `escrow` usa a API dedicada de Banking & Escrow e expõe o fluxo de
conta vinculada da Celcoin.

## Onboarding

1. Solicite uma URL pré-assinada com `requestDocumentUpload`.
2. Envie o arquivo para a URL retornada e use a `key` ao criar cada pessoa.
3. Cadastre as pessoas relacionadas com `createPerson`.
4. Crie a conta com `createAccount`, usando `accountType: ESCROW`, `personId`,
   `parties` e as permissões de cada parte.

As permissões são definidas na criação da conta e não devem ser tratadas como
alteráveis posteriormente.

## Depósito e movimentação

Use `createPosting` para solicitar uma operação `PIX`, `TED` ou `BOLETO`.
Para TED e Pix, cadastre antes o beneficiário com `createDestination`.
Quando `automaticallyApprove` for falso, revise a solicitação com
`reviewPosting`.

```java
var destination = client.escrow().createDestination(accountId, Map.of(
        "bank", "001", "branch", "0001", "account", "12345", "accountDigit", "6"));

var posting = client.escrow().createPosting(Map.of(
        "accountId", accountId,
        "amount", 1000.00,
        "type", "PIX",
        "accountDestinationId", destination.get("id"),
        "automaticallyApprove", false));

client.escrow().reviewPosting(String.valueOf(posting.get("id")),
        Map.of("approved", true));
```

Também estão disponíveis consulta de saldo/extrato, retenção automática de
depósitos, carteiras e cobranças, além de webhooks transacionais por conta.

Configure `CELCOIN_ESCROW_CLIENT_ID` e `CELCOIN_ESCROW_CLIENT_SECRET` para
habilitar chamadas ao ambiente contratado.
