# Pix BaaS - Implantação

O SDK implementa o produto **Pix BaaS** da Celcoin contra o ambiente de sandbox
(`https://sandbox.openfinance.celcoin.dev`). Esta página cobre a implantação, os
fluxos passivos (sem endpoint de criação), o bloqueio cautelar (webhook-only) e
as tabelas de erros dos fluxos de cash-out e portabilidade.

## Implantação

### Pré-requisitos

1. Configurar credenciais e ambiente em `application-local.yml` ou variáveis de
   ambiente (ver `.env.example`):

   ```yaml
   celcoin:
     base-url: https://sandbox.openfinance.celcoin.dev
     client-id: <client-id>
     client-secret: <client-secret>
   ```

2. A conta BaaS precisa existir no sandbox (o conjunto "agência/contas" usado nas
   chamadas DICT, cash-out e extrato). Sem ela, `GET /baas/v2/pix/dict/entry/external/{account}`
   retorna `CBE030`.

3. Registrar as chaves Pix na conta via `pix().createKey(...)` antes de gerar
   cobranças ou receber por chave.

### Capacidades implementadas

| Área | Métodos (`CelcoinPixOperations`) | Endpoint Celcoin |
| --- | --- | --- |
| QR dinâmico immediate | `createQrCode` | `POST /pix/v1/brcode/dynamic` |
| Cobrança estática | `createStaticChargeCashIn` | `POST /pix/v1/brcode/static` |
| QR duedate | `createDueDateQrCodeCashIn` | `POST /pix/v1/collection/duedate` |
| Consultar cobrança estática | `getStaticCharge` | `GET /pix/v1/brcode/static` |
| Status de recebimento | `getCashInReceipt` | `GET /pix/v2/receivement/v2/status` |
| Extrato de movimentações | `getMovements` | `GET /baas/v2/wallet/movement` |
| Devolução | `refund`, `getRefund`, `getDevolution` | `POST /baas-wallet-transactions-webservice/v1/pix/reverse` |
| Cash-out | `cashOut`, `cashOutToAccount`, `cashOutByKey` | `POST /baas/v2/pix/payment` |
| Cash-out por QR | `cashOutStaticQrCode`, `cashOutDynamicQrCode` | `POST /pix/v1/emv/full` + DICT + `POST /baas/v2/pix/payment` |
| Status do pagamento | `getStatus`, `getPaymentStatus` | `GET /baas/v2/pix/payment/status` |
| DICT lookup | `lookupKey` | `GET /baas/v2/pix/dict/entry/external/{account}` |
| Chaves (CRUD) | `createKey`, `listKeys`, `deleteKey`, `updateKeyName` | `POST/GET/DELETE/PUT /baas/v2/pix/dict/entry` |
| Split immediate/duedate | `createImmediateSplitQrCode`, `createDueDateSplitQrCode` | `POST /baas/v2/immediate/split`, `POST /baas/v2/duedate/split` |
| Agendamento | `schedule`, `getSchedule`, `cancelSchedule`, `listSchedules` | `POST /baas/v2/pix/payment` (com `scheduler`) + `/baas/v2/scheduler` |
| Portabilidade/reivindicação | `claimKey`, `confirmClaim`, `cancelClaim`, `getClaim`, `listClaims` | `POST/GET /baas/v2/pix/dict/claim` |
| Participantes do SPI | `participants` | `GET /pix/v1/participants` |

## Fluxos passivos (sem endpoint de criação)

- **Receber Pix Cash-in por agência e conta, por chave aleatória e por chaves
  individualizadas** não possuem endpoint de criação: o pagador inicia a
  transferência e a confirmação chega via webhook `pix-payment-in`. O SDK modela
  esse evento em `CelcoinPixCashInWebhookEvent`.
- **Bloqueio cautelar (recebimento e envio)** não possui API de criação/consulta:
  é aplicado automaticamente pelo motor antifraude da Celcoin e notificado via
  webhook. No cash-in o evento `pix-payment-in` chega com `status: PENDING`,
  `reason: BLOQUEIO CAUTELAR` e `blockedAmount`; no cash-out o evento
  `pix-payment-out` chega com `status: PENDING` e `reason: Sua transação está em análise`,
  seguido de `CONFIRMED` (aprovado) ou `ERROR` (`CBE171`/`PBE343`, rejeitado).

## Webhooks

Os modelos dos eventos são:

- `CelcoinPixCashInWebhookEvent` - evento `pix-payment-in` (inclui `amount`,
  `currentBalance`, `oldBalance`, `debitParty`, `creditParty`, `endToEndId` e os
  campos de bloqueio cautelar `reason`, `blockedAmount`, `expireDate`).
- `CelcoinPixCashOutWebhookEvent` - evento `pix-payment-out` (acrescenta
  `clientCode` e `error{errorCode, message}`).
- Devoluções: `pix-reversal-in` (devolução recebida sobre cash-out) e
  `pix-reversal-out` (devolução feita sobre cash-in).

## Tabela de erros - Cash-out

| Código | Significado | Ação |
| --- | --- | --- |
| `CBE030` | Conta/agência não encontrada no DICT | Revisar a conta BaaS e a chave informada |
| `CBE150` | Falha na liquidação do pagamento | Consultar `getPaymentStatus`/`getDevolution` para diagnóstico |
| `CBE171` | Pagamento rejeitado por bloqueio cautelar | Aguardar análise; evento `pix-payment-out` com `status: ERROR` |
| `PBE343` | Erro de negociação no SPI | Reavaliar `creditParty`/`debitParty` |
| `SCH036` | Agendamento sem `schedulerDate` | Informar a data no `CelcoinPixScheduleRequest` |
| `SCH079` | Não é possível cancelar no dia da execução | Cancelar antes do dia de execução |
| `SCH085` | Agendamento duplicado para a mesma data | Usar outra data ou cancelar o agendamento anterior |
| `CBE620` | Split: mais de 5 itens em `feeDetails` | Reduzir o número de divisões |
| `CBE621` | Split: `accountCredit` obrigatório | Informar a conta receptora do split |
| `CBE622`/`CBE626` | Split: soma dos `feeDetails` difere de `totalAmount` | Conferir os valores |
| `CBE623` | Split: `totalAmount` obrigatório | Informar o valor total |
| `CBE624` | Split: percentual excede o limite | Respeitar o teto (padrão 10%) |

## Tabela de erros - Portabilidade e Reivindicação

- `claimType`: `PORTABILITY` (mesmo titular; CPF/CNPJ/EMAIL/PHONE) ou `OWNERSHIP`
  (titular diferente; apenas PHONE).
- `Status` de uma reivindicação: `OPEN`, `WAITING_RESOLUTION`, `CONFIRMED`,
  `CANCELLED`, `COMPLETED`.
- `reason` em `confirmClaim`/`cancelClaim`: `USER_REQUESTED`, `ACCOUNT_CLOSURE`,
  `FRAUD`, `DEFAULT_OPERATION`.
- A consulta por lista omite `donorAccount` quando a Celcoin é apenas o PSP que
  reivindica (sigilo BACEN). Webhooks associados: `pix-dict-claim-open`,
  `pix-dict-claim-waiting`, `pix-dict-claim-confirmed`, `pix-dict-claim-completed`,
  `pix-dict-claim-cancelled`.

## Observações

- A base de sandbox usa `sandbox.openfinance.celcoin.dev`; produção BaaS usa
  `apicorp.celcoin.com.br`.
- Consulta de status de pagamento válida até ~20s após a criação, com janela de
  consulta de 8 dias.
- A consulta de cobrança estática (`getStaticCharge`) está implementada, mas o
  endpoint `GET /pix/v1/brcode/static` está marcado como "em revisão" pela
  Celcoin e retorna 404 no sandbox até que seja restabelecido.
- As operações de escrita usam idempotência via header `Idempotency-Key`
  (persistida pelo `CelcoinIdempotencyService`).
