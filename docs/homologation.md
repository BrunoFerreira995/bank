# Homologação Celcoin

Este guia organiza a preparação, execução e entrega da homologação do SDK. A
homologação oficial deve ser executada pela aplicação integrada, reproduzindo a
jornada real do usuário. A coleção HTTP deste repositório serve apenas para
smoke tests técnicos e não substitui as evidências da aplicação.

## Identificação da entrega

| Campo | Valor |
|---|---|
| Razão social | `<RAZAO_SOCIAL>` |
| CNPJ | `<CNPJ>` |
| Produto/roteiro | `<BAAS ou produto avulso>` |
| Ambiente | `<sandbox/homologação>` |
| Versão da aplicação | `<VERSAO>` |
| Responsável técnico | `<NOME E E-MAIL>` |
| Data da execução | `<AAAA-MM-DD>` |

O arquivo final deve ser enviado em PDF com o título `Roteiro de homologação BaaS - <Razão social>` ou `Roteiro de homologação <Produto> - <Razão social>`.

## Pré-requisitos

- Credenciais configuradas fora do controle de versão.
- Produto contratado e habilitado para o ambiente.
- Conta Celcoin e contas BaaS de teste disponíveis.
- Endpoint de webhook público, com autenticação configurada quando exigida.
- Logs correlacionando request, resposta, `clientRequestId`, `transactionId`,
  `endToEndId` ou outro identificador retornado pela Celcoin.
- Evidências sem tokens, client secrets, certificados privados ou dados
  pessoais desnecessários.

## Roteiro por produto

Execute somente os produtos contratados e integrados:

| Produto | Fluxos mínimos | Evidência principal |
|---|---|---|
| Autenticação | obter token e renovar após expiração | tela/log sem segredo + status |
| Onboarding | PF, PJ, consulta de status e documentos | jornada + onboardingId |
| Contas | criação, consulta, saldo, extrato e encerramento | tela/log + accountId |
| Pix | cash-in, cash-out, QR Code, devolução e webhook | comprovante + transactionId/endToEndId |
| Boletos | autorização, pagamento e status | comprovante + clientRequestId |
| TED | envio, status, devolução e webhook | comprovante + id/clientCode |
| Cartões | emissão, ativação, bloqueio, limite e transações | tela/log + cardId |
| Crédito | cadastro, simulação, proposta e status | tela/log + identificador |
| Débito veicular | consulta, regras e pagamento | consulta/pagamento + IDs |

Para cada cenário, registre data/hora, passos, esperado, observado,
identificadores Celcoin, screenshot/vídeo e log sanitizado.

## Execução e entrega

1. Execute os cenários da [matriz de testes](homologation-test-matrix.md).
2. Preencha o [checklist de evidências](homologation-evidence.md).
3. Anexe imagens/vídeos ou logs sem segredos.
4. Gere o PDF final e revise empresa, CNPJ e produto.
5. Envie o roteiro ao analista de onboarding/homologação e registre o protocolo.
6. Corrija itens rejeitados e mantenha uma nova versão do roteiro.

Após a aprovação integral, a Celcoin inicia as validações contratuais e a
liberação das credenciais de produção.
