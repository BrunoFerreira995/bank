# cel_banking - cards

O módulo `cards` integra a solução `cel_card` da Celcoin para contas cartão,
emissão e reemissão, limites, ativação, PIN, rastreio, webhooks e faturas
pós-pagas.

O fluxo básico é:

1. concluir o onboarding do portador e obter aprovação;
2. criar a conta cartão com `createCardAccount`;
3. emitir o cartão com `issueCard`;
4. ativar o cartão físico após o recebimento.

Os endpoints utilizam o prefixo `/cards/v1` e aceitam a mesma autenticação
OAuth 2.0 do SDK. Operações de escrita recebem uma chave de idempotência.
Cartões pré-pagos e multiapp exigem uma conta BaaS ativa vinculada à conta
cartão; produtos pós-pagos dependem de limite de crédito configurado. A
criação da conta é assíncrona e deve ser acompanhada pelo webhook
`account-created`. A simulação de transação e a simulação de rastreio são
exclusivas do sandbox.

```java
var account = celcoinClient.cards().createCardAccount(request, "card-account-1");
var card = celcoinClient.cards().issueCard(
    new CelcoinCardIssueRequest(account.cardAccountId(), "MARIA SILVA", "PHYSICAL", null, null),
    "issue-card-1");
```
