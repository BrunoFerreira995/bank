# Emissão de Boletos

O módulo `boletos()` suporta emissão BaaS de Boleto/BolePix, consulta, PDF,
consulta paginada e cancelamento.

```java
var request = new BoletoDtos.CelcoinBoletoIssueRequest(
        "pedido-123", null, 5, "2026-12-30", new BigDecimal("10.00"),
        "[email protected]", null,
        Map.of("name", "Pagador", "document", "12345678901"),
        Map.of("account", "30023646056263"),
        Map.of("fine", 1), List.of(Map.of("account", "300000000001", "amount", 1)),
        null, List.of("Pedido 123"), "BOLEPIX");

var boleto = celcoin.boletos().issue(request);
byte[] pdf = celcoin.boletos().downloadPdf(boleto.boletoId());
```

O PDF só pode ser gerado enquanto a cobrança estiver pendente. No sandbox,
utilize os dados oficiais de teste da Celcoin.
