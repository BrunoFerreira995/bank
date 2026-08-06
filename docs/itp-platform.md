# Plataforma de Iniciação ITP / Open Keys

O SDK separa os fluxos da Plataforma de Iniciação em três agregados:

- `client.itp()`: consentimento e pagamento Pix com redirecionamento;
- `client.jsr()`: vínculo de conta, FIDO2 e pagamento sem redirecionamento;
- `client.pixAuto()`: consentimento, agendamento, liquidação e gestão do Pix
  Automático;
- `client.sweeping()`: consentimentos e pagamentos do Pix Inteligente/Sweeping
  Accounts.

## Jornada com redirecionamento

`CelcoinItpOperations` expõe criação de consentimento, callback, consulta da
iniciação, criação do Pix, parser de webhook, máquina de estados e catálogo de
erros. Há aliases semânticos para `createInstantPayment` e
`createScheduledPayment`; ambos usam o mesmo contrato de consentimento, com a
data de execução no DTO.

Fluxo:

1. Criar o consentimento.
2. Redirecionar o cliente para `authorizationUrl`.
3. Processar `code`, `state` e `id_token` no callback.
4. Criar o Pix e acompanhar `PDNG`, `ACSP`, `ACSC` ou `RJCT` por webhook.
5. Consultar a iniciação como contingência.

## Jornada sem redirecionamento e aproximação

`CelcoinJsrOperations` cobre enrollment, opções/registro FIDO, assinatura,
iniciação v4 e criação do Pix. Pix por aproximação é uma experiência de
aplicativo baseada em NFC, vínculo prévio e FIDO2; o SDK fornece as APIs da JSR,
enquanto NFC, deeplink e a interface autenticada permanecem no aplicativo do
contratante.

## Portal Open Keys

Dashboard, relatórios, configurações, demonstrações e área de gestão são
recursos do portal Open Plus/Open Keys. Como não há contrato público de API
administrativa para esses recursos, eles permanecem documentados como
dependentes do portal/contratação e não são simulados como endpoints do SDK.

## Referências oficiais

- [Plataforma ITP](https://developers.celcoin.com.br/docs/solu%C3%A7%C3%A3o-de-pagamentos-itp)
- [APIs ITP](https://developers.celcoin.com.br/docs/apis-itp)
- [Portal Open Keys](https://developers.celcoin.com.br/docs/portal-de-configura%C3%A7%C3%B5es-do-open-keys)
- [Jornada sem redirecionamento](https://developers.celcoin.com.br/docs/inicia%C3%A7%C3%A3o-de-pagamento-sem-redirecionamento)
- [Pix por aproximação](https://developers.celcoin.com.br/docs/pix-por-aproxima%C3%A7%C3%A3o)
- [FAQ Open Keys](https://developers.celcoin.com.br/docs/faq-open-keys)
