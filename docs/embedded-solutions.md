# cel_banking — Embedded Solutions

O SDK expõe as APIs avulsas por `celcoinClient.embedded()`. Os módulos
especializados de Pix, recargas, débitos veiculares, boletos e SLC continuam
disponíveis diretamente na fachada.

## DDA

```java
client.embedded().ddaRegister(Map.of(
        "document", "23155663049",
        "clientName", "Cliente",
        "clientRequestId", "dda-001"), "dda-001");

client.embedded().ddaRegisterInvoices(Map.of(
        "document", List.of("23155663049")), "dda-invoice-001");
```

O cadastro e a exclusão do DDA são assíncronos e devem ser acompanhados pelos
webhooks de `Subscription`, `Deletion` e `Invoice`.

## Pagamento de contas

O fluxo é `billAuthorize` → `billReserve` → confirmação explícita com
`billCapture`. Em caso de confirmação rejeitada ou operação fora da janela de
liquidação, use `billReverse`. `billStatus`, `billOccurrences` e
`billInstitutions` apoiam a conciliação operacional.

## NFS-e

O módulo oferece cadastro de empresa, consulta de empresa, emissão, consulta e
cancelamento de NFS-e. A emissão é assíncrona e exige certificado A1 da empresa
emissora, inclusive no sandbox.

## Conta Celcoin, TED e conciliação

`celcoinAccountBalance` consulta o saldo da conta proprietária. As operações
`tedTransfer` e `tedStatus` integram o fluxo TED contratado. Para conciliação,
`reconciliation(resource, filters)` preserva o recurso e os filtros do contrato
específico sem acoplar o SDK a uma variante de API.
