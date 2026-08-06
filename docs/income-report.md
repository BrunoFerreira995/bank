# Informe de Rendimentos

O SDK consulta o informe financeiro da conta pelo endpoint oficial
`GET /baas/v2/account/income-report`.

Para pessoa física, o informe é anual e deve usar o ano-calendário anterior. Para
pessoa jurídica, informe também o trimestre (`1` a `4`). O retorno contém os dados
estruturados e o PDF em Base64 (`body.incomeFile`), protegido pelos seis primeiros
dígitos do CPF ou CNPJ do titular.

```java
var pf = celcoin.accounts().getIncomeReport("300578778788", 2024);
var pj = celcoin.accounts().getIncomeReportPj("300578778788", 2024, 4);
String pdfBase64 = pj.body().incomeFile();
```
