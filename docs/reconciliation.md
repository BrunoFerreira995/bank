# Conciliação

O SDK expõe `client.reconciliation()` para consultar os tipos de arquivo,
extrair movimentações contábeis e consultar o extrato consolidado da Celcoin.
As consultas são normalmente disponibilizadas em D+1.

```java
var types = client.reconciliation().listFileTypes();

var movement = client.reconciliation().extractFile(
        new CelcoinReconciliationDtos.ExportFileRequest(
                1, LocalDate.of(2026, 8, 5), 1, 1000));

var statement = client.reconciliation().consolidatedStatement(
        new CelcoinReconciliationDtos.ConsolidatedStatementRequest(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5), 1, 1000));
```

## Endpoints

| Operação | Endpoint |
| --- | --- |
| Tipos de arquivo | `GET /tools-conciliation/v1/exportfile/types` |
| Extração de arquivo | `GET /tools-conciliation/v1/exportfile` |
| Extrato consolidado | `GET /tools-conciliation/v1/ConsolidatedStatement` |

`fileType` e `accountDate` são obrigatórios na extração. `startDate` e
`endDate` são obrigatórios no extrato consolidado, e o SDK rejeita intervalos
maiores que 15 dias. Os campos de movimento são mantidos em `Map` porque o
layout varia conforme o produto (Pix, boleto, recarga, TED, débito veicular e
outros).

## Erros

Use `client.reconciliation().error(codigo)` para classificar erros publicados,
como `004` (tipo inexistente), `005` (extrato vazio), `991` (atributo inválido),
`992` (sem permissão), `993` (token expirado), `994` (header ausente), `995`
(não autorizado) e `999` (falha da operação).

Referências: [API de conciliação](https://developers.celcoin.com.br/docs/sobre-api-de-concilia%C3%A7%C3%A3o),
[extrato consolidado](https://developers.celcoin.com.br/docs/sobre-api-de-extrato-consolidado) e
[tabela de erros](https://developers.celcoin.com.br/docs/tabela-de-erros-conciliacao).
