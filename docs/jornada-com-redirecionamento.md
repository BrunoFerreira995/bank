# Jornada com Redirecionamento

O agregado `client.jsr()` expõe as APIs de iniciação v4 e sessões de jornada
com redirecionamento para a detentora de conta.

## Fluxo

1. Crie a sessão com `createPaymentJourney(...)` e redirecione o usuário para
   `journeySessionUrl` retornado.
2. Consulte a sessão por `getPaymentJourney(...)` ou liste sessões com
   `listPaymentJourneys(...)`.
3. Crie a iniciação v4 com `createPaymentInitiation(...)` quando a integração
   usar o registro de payment initiation diretamente.
4. Após a autorização, processe o callback com `processCallback(...)`.
5. Execute o pagamento com `createPix(...)`.
6. Liste ou consulte as iniciações com `listPaymentInitiations(...)` e
   `getPaymentInitiation(...)`.

Os campos de `paymentInitiationData`, `tags` e `settings` são mantidos como
mapas para suportar as regras configuráveis da jornada e a evolução do
contrato Open Finance. Use HTTPS e somente URLs de callback previamente
autorizadas para a aplicação.

Endpoints implementados:

```text
POST /open-keys/itp/api/v2/payments/v4/journeys-sessions
GET  /open-keys/itp/api/v2/payments/v4/journeys-sessions
GET  /open-keys/itp/api/v2/payments/v4/journeys-sessions/{id}
POST /open-keys/itp/api/v2/payments/v4/payment-initiation
GET  /open-keys/itp/api/v2/payments/v4/payment-initiation
GET  /open-keys/itp/api/v2/payments/v4/payment-initiation/{id}
POST /open-keys/itp/api/v2/payments/v4/payment-initiation/{id}/pix
```

Referências: [criação de iniciação v4](https://developers.celcoin.com.br/docs/criar-inicia%C3%A7%C3%A3o-de-pagamento-1),
[criação de jornada](https://developers.celcoin.com.br/docs/criar-jornada-de-pagamento),
[listagem de iniciações](https://developers.celcoin.com.br/reference/get_open-keys-itp-api-v2-payments-v4-payment-initiation-1)
e [PIX v4](https://developers.celcoin.com.br/docs/pix).
