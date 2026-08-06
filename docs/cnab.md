# CNAB

O módulo `cnab()` processa arquivos de remessa e disponibiliza consulta e
download dos arquivos de entrada e retorno.

```java
var submitted = client.cnab().process(
    new CelcoinCnabProcessRequest(
        Path.of("/tmp/remessa.rem"), "remessa-2026-08-06", accountId),
    "cnab-remessa-2026-08-06");

var status = client.cnab().getStatus(submitted.fileId());
byte[] retorno = client.cnab().downloadOutput(submitted.fileId());
```

O SDK valida arquivo regular, `clientRequestId` e o limite de 10 MB antes do
upload. A Celcoin identifica automaticamente CNAB 240, CNAB 400 ou formato
inválido. O processamento é assíncrono; consulte os estados `PENDING`,
`PROCESSING`, `ERROR`, `FINISHED`, `GENERATING_OUTPUT`,
`FAILED_GENERATING_OUTPUT` e `GENERATED_OUTPUT`.

Endpoints:

- `POST /baas/v2/cnab-file` — upload multipart (`file`, `clientRequestId`, `account`);
- `GET /baas/v2/cnab-file/{id}` — consulta por `fileId` ou `clientRequestId`;
- `GET /baas/v2/cnab-file/{id}/fileinput` — remessa;
- `GET /baas/v2/cnab-file/{id}/fileoutput` — retorno.

O produto precisa estar contratado/habilitado e a conta BaaS informada deve
ser a conta de origem dos pagamentos ou de recebimento das cobranças.
