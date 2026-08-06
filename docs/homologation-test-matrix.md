# Matriz de Massa de Testes para Homologação

Esta matriz não contém credenciais nem substitui a massa oficial da Celcoin.
Preencha os valores fornecidos para cada ambiente e preserve os identificadores
gerados durante a execução.

| Ambiente | Base URL | Conta de teste | Documento | Webhook | Observações |
|---|---|---|---|---|---|
| Sandbox | `https://sandbox.openfinance.celcoin.dev` | `<ACCOUNT_SANDBOX>` | `<DOCUMENTO_SANDBOX>` | `<WEBHOOK_SANDBOX>` | 24/7 conforme produto |
| Homologação | `<BASE_URL_HML>` | `<ACCOUNT_HML>` | `<DOCUMENTO_HML>` | `<WEBHOOK_HML>` | preencher com Celcoin |
| Produção assistida | `<BASE_URL_PROD>` | `<ACCOUNT_PROD>` | `<DOCUMENTO_PROD>` | `<WEBHOOK_PROD>` | após aprovação |

| ID | Produto | Cenário | Dados necessários | Identificador | Resultado esperado |
|---|---|---|---|---|---|
| AUTH-01 | Auth | obter token | client credentials | expiry/correlation | HTTP 200 |
| ACC-01 | Contas | criar PF/PJ | documento único | onboardingId/accountId | confirmado |
| ACC-02 | Contas | saldo/extrato | accountId | accountId | dados retornados |
| PIX-01 | Pix | QR e pagamento | chave/valor de teste | transactionId/endToEndId | confirmado + webhook |
| PIX-02 | Pix | devolução | transação confirmada | endToEndId | devolução processada |
| BILL-01 | Boletos | autorizar/pagar | massa oficial | transactionId/clientRequestId | confirmado |
| TED-01 | TED | enviar | dados da massa | id/clientCode | PROCESSING/CONFIRMED |
| TED-02 | TED | devolução | TED elegível | originalId/reason | webhook reversal |
| CARD-01 | Cartões | emitir/ativar/bloquear | accountId | cardId | estados corretos |
| CREDIT-01 | Crédito | simular/propor | borrower/offer | proposalId | status esperado |
| VEH-01 | Veicular | consultar | UF/placa/Renavam | idConsult/clientRequestId | débitos recebidos |
| VEH-02 | Veicular | pagar | debtIdList válida | paymentId/clientRequestId | pagamento aceito |
| WH-01 | Webhook | evento duplicado | evento real | externalEventId | idempotência |

Repita os cenários com dados inválidos, saldo insuficiente, recurso inexistente,
timeout e webhook indisponível quando a massa contratada permitir.
