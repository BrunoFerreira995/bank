# Recargas

O módulo `topups()` consulta operadoras e valores disponíveis e executa o
fluxo de reserva/recarga BaaS.

```java
var providers = client.topups().listProviders(new TopupDtos.ProviderQuery(11, 2, 1));
var values = client.topups().listValues(new TopupDtos.ProviderValuesQuery(11, 2087));
var topup = client.topups().reserve(new TopupDtos.TopupRequest(
    account, "topup-001", new BigDecimal("13.00"), 2087, null, List.of(),
    Map.of("countryCode", 55, "stateCode", 11, "number", "994114386")), "topup-001");
var status = client.topups().getStatus(topup.transactionId(), "topup-001");
```

Operadoras devem ser armazenadas/cacheadas por até um dia; os valores devem
ser consultados no fluxo, pois podem mudar. Recargas digitais podem retornar
PIN e são confirmadas automaticamente; recargas online/telefonia exigem
`capture` com o identificador retornado.

Endpoints:

- `GET /baas/v2/topup/providers`
- `GET /baas/v2/topup/provider-values`
- `POST /baas/v2/topup`
- `GET /baas/v2/topup`
- `PUT /v5/transactions/topups/{transactionId}/capture`

O sandbox não tem massa fixa: use os `providerId` e valores retornados pelas
consultas. A API de recargas não deve ser tratada como webhook-driven; consulte
o status após intermitência ou erro inesperado.

Os erros ficam disponíveis em `client.topups().errors()`.
