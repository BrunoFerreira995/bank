# Pix Indireto

O módulo `indirectPix()` usa as APIs específicas do participante indireto
(`pix-indirect/v1`). Ele não deve ser confundido com `pix()`, que opera os
fluxos BaaS/Core Banking da conta Celcoin.

## Pré-requisitos e adesão

O participante precisa de contrato com a Celcoin, diligência técnica,
credenciais de homologação, cadastro regulatório aplicável, homologação Pix e
operação restrita antes da produção. A habilitação de DICT, chaves, infrações
e MED é operacional e contratual.

## Capacidades

- DICT: consultar chave, verificar lote de chaves, cadastrar, excluir e listar
  todas as chaves de uma conta.
- Chaves: portabilidade, reivindicação e consulta de processos.
- Infrações: abrir, consultar, listar e fechar relato.
- MED 2.0: criar, consultar, cancelar e atualizar recuperação de valores;
  consultar o grafo, solicitar devolução e fechar infração/devolução.
- Pagamentos: iniciar cash-out, consultar pagamento e recebimento, reverter
  recebimento, gerar/decodificar QR Code e reportar transações internas fora do
  SPI.
- Pix Saque/Troco: usar `transactionType=WITHDRAWAL` ou `CHANGE` no payload de
  pagamento, conforme o contrato do participante.
- Webhooks: `parseWebhook` preserva `entity`, `status`, `body`, `error` e o
  payload original; `parseCashInAuthorization` normaliza a resposta de
  autorização (`ACCEPTED`/`DENIED`).

As requisições de infração e MED usam `Map<String, Object>` deliberadamente:
os campos regulatórios evoluem por versão e devem ser enviados exatamente como
definidos no contrato habilitado. Operações mutáveis aceitam
`Idempotency-Key`.

## Regras de segurança

Não use `keychecker` em fluxo transacional; ele é destinado à sanitização de
base e contatos. Registre `endToEndId`, `transactionId`, `infractionReportId`,
`fundsRecoveryId` e correlation/request ID. Não feche um MED antes de executar
a devolução exigida pelo estado da solicitação.

Referências: [visão geral do Pix Indireto](https://developers.celcoin.com.br/docs/sobre-o-pix-indireto),
[listagem de chaves](https://developers.celcoin.com.br/docs/listar-chaves-pix-de-um-cliente),
[pagamento](https://developers.celcoin.com.br/reference/iniciar-pagamento-pix-indireto),
[autorização de cash-in](https://developers.celcoin.com.br/docs/autoriza%C3%A7%C3%A3o),
[reversão](https://developers.celcoin.com.br/docs/devolu%C3%A7%C3%A3o-de-recebimentos-pix-copy),
[reporte fora do SPI](https://developers.celcoin.com.br/docs/transa%C3%A7%C3%B5es-fora-do-spi-reporte-bacen),
[MED 2.0](https://developers.celcoin.com.br/docs/criar-recupera%C3%A7%C3%A3o-de-valores) e
[webhooks](https://developers.celcoin.com.br/docs/modelos-de-webhooks-do-pix-indireto).
