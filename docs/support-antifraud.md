# Suporte, tickets e antifraude

## Escopo implementado

O SDK mantém o recebimento idempotente e auditável de webhooks em `celcoin_webhook_event`.
Eventos de antifraude/FtM podem ser normalizados com:

```java
CelcoinAntifraudEvent event = client.webhooks().parseAntifraud(payload);
```

O parser extrai entidade, status, transação, end-to-end, motivo e valor, e classifica a decisão como
`ALLOWED`, `BLOCKED`, `PENDING`, `RELEASED`, `REJECTED` ou `UNKNOWN`. A classificação é local e não
substitui a decisão da Celcoin.

Para retenção, a aplicação deve executar sua política de agendamento:

```java
client.webhooks().purgeEventsBefore(OffsetDateTime.now().minusDays(365));
```

A operação retorna a quantidade removida. Defina o prazo conforme contrato, compliance e política de
retenção da instituição.

## Itens dependentes do portal/contrato

- **Central de ajuda e SLA:** conteúdo, abertura de chamados e tempos de atendimento são disponibilizados
  pelo canal de suporte contratado; não há endpoint público no contrato REST usado pelo SDK.
- **Listagem de tickets:** permanece uma função do Painel do Cliente/Celcoin; o SDK não cria uma API
  administrativa fictícia para tickets.
- **Follow the Money:** regras, PLD, usuários, filas e relatórios são configurados no Painel e dependem
  de habilitação contratual. O SDK oferece o receptor, a persistência e a normalização dos eventos, mas
  não expõe gerenciamento de regras sem contrato público.
- **Regras por transação:** o resultado recebido da Celcoin é preservado no payload original e pode ser
  interpretado pelo parser; a parametrização da regra ocorre no produto antifraude contratado.

## Auditoria

O endpoint local de webhook valida assinatura, deduplica pelo identificador externo, registra payload e
headers, controla processamento/retry e permite consulta/reprocessamento administrativo. Não registre
segredos nos headers e aplique mascaramento adicional antes de encaminhar payloads para logs.
