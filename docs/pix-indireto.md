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

- DICT: consultar chave, verificar lote de chaves, cadastrar e excluir chave.
- Chaves: portabilidade, reivindicação e consulta de processos.
- Infrações: abrir, consultar, listar e fechar relato.
- MED: criar, consultar, cancelar e fechar solicitação de devolução.

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
[pré-requisitos](https://developers.celcoin.com.br/docs/pr%C3%A9-requisitos-do-participante),
[infrações](https://developers.celcoin.com.br/docs/infra%C3%A7%C3%B5es) e
[MED](https://developers.celcoin.com.br/docs/mecanismo-especial-de-devolu%C3%A7%C3%B5es-med).
