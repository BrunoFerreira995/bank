# Massa de Testes Open Finance

Esta matriz reproduz os cenários oficiais de jornadas de pagamento. Os testes
locais validam o catálogo, as transições e as validações do SDK; a execução
contra o sandbox exige credenciais, uma detentora habilitada e a massa de contas
fornecida pela Celcoin.

| ID | Cenário | Etapa | Resultado esperado |
| --- | --- | --- | --- |
| JSR-LIM-001 | Valor máximo do vínculo Pix | PIX | `CONSUMED` |
| JSR-LIM-001.1 | Valor máximo do vínculo e limite diário | PIX | `CONSUMED` |
| JSR-LIM-002 | Acima do limite do vínculo | PIX | `RJCT` / `VALOR_ACIMA_LIMITE` |
| JSR-SAL-003 | Saldo insuficiente | PIX | `RJCT` / `SALDO_INSUFICIENTE` |
| JSR-MIX-004 | Limite e saldo insuficiente simultâneos | PIX | `RJCT`; registrar a precedência retornada |
| JSR-LIMD-005 | Acima do limite diário | PIX | `RJCT` / limite diário |
| JSR-LIMN-006 | Acima do limite noturno | PIX | `RJCT` / limite noturno |
| JSR-CONS-007 | Consentimento expirado | PIX | `RJCT` / consentimento expirado |
| JSR-CONS-008 | Consentimento revogado | PIX | `RJCT` / consentimento revogado |
| JSR-FIDO-011 | Vínculo rejeitado por timeout | Vínculo | `REJECTED` / timeout |

## Execução

Para os cenários JSR-LIM, JSR-SAL, JSR-MIX, JSR-LIMD e JSR-LIMN, conclua o
enrollment FIDO, configure os limites no sandbox e crie o payment initiation
com o valor da tabela. Para JSR-CONS-007 e JSR-CONS-008, deixe o consentimento
expirar ou revogue-o na detentora antes de chamar o PIX. Para JSR-FIDO-011,
crie o vínculo e deixe a janela de enrollment expirar.

Em cada cenário, capture: `paymentInitiationId`, status do consentimento,
`paymentId`, `endToEndId`, status do Pix, `rejectionReason`, resposta HTTP e
evento de webhook. Pagamentos assíncronos devem ser confirmados por webhook ou
consulta, nunca apenas pelo HTTP inicial.

O catálogo executável está em `OpenFinanceTestScenarios.paymentJourneys()` e
é validado por `OpenFinanceTestScenariosTest`.

Fonte: [massa oficial de testes da Celcoin](https://developers.celcoin.com.br/docs/massa-de-testes).
