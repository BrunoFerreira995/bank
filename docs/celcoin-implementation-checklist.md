# Checklist de Implementação Celcoin

Legenda:

- `[x]` já existe no SDK atual.
- `[ ]` pendente.
- `[~]` estrutura preparada, aguardando contrato oficial de endpoint/payload ou
  dependência de ambiente/produto externo.

## Introdução

- [x] Escrever uma breve introdução funcional do SDK e dos produtos Celcoin.
- [x] Documentar credenciais de acesso via `.env.example` e `CelcoinProperties`.
- [x] Documentar segurança da API.
- [x] Implementar base de idempotência das APIs.
- [x] Implementar persistência e consulta completa de idempotência por operação.
- [x] Preparar certificado mTLS com `CelcoinSslContextProvider`.
- [x] Implementar mTLS no `WebClient`.
- [x] Implementar controle de taxa ou integração com headers de rate-control da Celcoin.
- [x] Implementar primeira consulta funcional: autenticação `POST /v5/token`.
- [x] Implementar arquivo de movimentação via SFTP (`docs/movement-files.md`).
- [~] Avaliar e implementar servidor MCP da Celcoin: não há protocolo/servidor oficial Celcoin fornecido para este SDK.

## Validação no Sandbox

Status da integração com o ambiente de sandbox (`https://sandbox.openfinance.celcoin.dev`):

- [x] Credenciais de sandbox configuradas em `application-local.yml` e `.env.example`.
- [x] Autenticação `POST /v5/token` validada (HTTP 200, token OAuth com validade de 2400s).
- [x] Primeira consulta funcional `GET /pix/v1/participants` validada (lista real de participantes do SPI).
- [x] Criação de QR Code dinâmico `POST /pix/v1/brcode/dynamic` validada (transactionId `4000715670`, EMV gerado).
- [x] Decodificação de EMV `POST /pix/v1/emv/full` validada (type/key/amount/transactionIdentification).
- [x] Cobrança estática `POST /pix/v1/brcode/static` validada (transactionId `40000046501`; `amount` deve ser numérico).
- [x] Autenticação `POST /v5/token` via `application/x-www-form-urlencoded` (SDK já usa form-urlencoded).
- [~] Suíte automatizada: compilação principal passa; execução WireMock no sandbox local depende de permissão para abrir portas.
- [x] Validação das migrations Flyway via Testcontainers (PostgreSQL em Docker).

## Homologação

- [x] Criar guia de homologação por produto.
- [x] Criar massa de testes por ambiente.
- [x] Criar checklist de evidências para aprovação.
- [x] Criar coleção de requests para homologação.

## Onboarding KYC

- [x] Criar pacote base `onboarding`.
- [x] Criar interface pública `CelcoinOnboardingOperations`.
- [x] Criar cliente HTTP `CelcoinOnboardingClient`.
- [x] Registrar `CelcoinOnboardingOperations` no `CelcoinAutoConfiguration`.
- [x] Expor operações de onboarding em `CelcoinClient`.
- [x] Mapear endpoints oficiais de onboarding KYC.
- [x] Implementar criação de onboarding PF.
- [x] Implementar criação de onboarding PJ.
- [x] Implementar consulta de status de onboarding.
- [x] Implementar atualização ou complementação cadastral.
- [x] Implementar envio de documentos (via `files[]` com URL pública dentro da proposta).
- [~] Implementar autenticação biométrica: `SELFIE` é suportado em `files[]`, mas a jornada biométrica é produto separado.
- [~] Implementar validação de prova de vida: depende da jornada WebView/contrato Celcoin, sem endpoint REST dedicado.
- [x] Criar DTOs de KYC PF.
- [x] Criar DTOs de KYC PJ.
- [x] Criar DTOs de documentos e anexos.
- [x] Criar DTOs de resposta e status de análise.
- [x] Mapear webhooks de onboarding KYC.
- [x] Mapear tabela de erros de onboarding.
- [x] Adicionar exemplos de uso em `docs/examples.md`.
- [x] Documentar fluxo funcional de onboarding KYC em `docs/onboarding.md`.
- [x] Implementar testes unitários de serialização dos DTOs.
- [x] Implementar testes WireMock de onboarding.

## cel_banking - BaaS & Core

### Sobre o BaaS & Core Banking

- [x] Documentar visão geral do BaaS & Core Banking.
- [x] Documentar FAQs.
- [x] Documentar diretriz de Termos de Uso - BaaS.

### Follow the Money

- [~] Mapear Follow the Money: configuração e operação dependem de contrato/Painel; não há endpoints públicos no contrato consultado.
- [~] Implementar DTOs e interfaces: aguardando contrato público de integração.
- [x] Documentar o tratamento de decisões e webhooks relacionados.

### APP e Internet Banking Whitelabel

- [x] Documentar integração MyBenk.
- [~] Mapear APIs disponíveis: canais white-label dependem de contratação e configuração Celcoin.

### Painel do Cliente

- [x] Documentar recursos do painel do contratante.
- [~] Mapear APIs administrativas disponíveis: acesso pelo portal e contrato específico.

### Abertura de Contas KYC

- [x] Implementar criação de contas via onboarding.
- [x] Implementar informações financeiras.
- [x] Implementar webhooks de onboarding.
- [x] Documentar cenários de status de onboarding para sandbox.
- [~] BC Protege+ executado automaticamente pela Celcoin; não há endpoint de integração dedicado.
- [x] Criar cenários práticos de abertura de conta PJ.
- [x] Implementar onboarding sem WebView usando URLs públicas em `files[]`.
- [x] Criar FAQs de abertura de contas.

### Gestão de Contas

- [x] Criar interface de gestão de contas.
- [x] Criar DTOs mínimos de conta.
- [x] Criar conta apenas Core Banking.
- [x] Implementar informações financeiras de conta.
- [x] Atualizar dados do cliente.
- [x] Desativar ou encerrar conta.
- [x] Listar contas.
- [x] Mapear tabela de erros de gestão de contas.
- [x] Consultar número de contas.
- [x] Implementar bloqueios judiciais.
- [x] Implementar atualização de status de conta.
- [x] Adicionar saldo em sandbox.
- [x] Implementar monitoramento cadastral.
- [~] Implementar webhooks de monitoramento cadastral.
- [~] Implementar simulação de monitoramento cadastral.

### Relatórios

- [x] Consultar saldo.
- [x] Consultar saldo do dia e movimentações diárias consolidadas.
- [x] Consultar extrato.
- [~] Consultar extrato detalhado.
- [~] Consultar transações do extrato.
- [x] Implementar paginação oficial dos relatórios.
- [x] Implementar testes WireMock dos relatórios.

## Pix BaaS

### Sobre o Pix

- [x] Documentar implantação Pix.
- [x] Criar interfaces públicas Pix.
- [x] Criar DTOs mínimos Pix.

### Cash-in

- [x] Receber Pix Cash-in por agência e conta.
- [x] Receber Pix Cash-in por chave aleatória.
- [x] Receber Pix Cash-in por chaves individualizadas.
- [x] Receber Pix Cash-in por cobrança estática.
- [x] Receber Pix Cash-in por QR Code dinâmico immediate.
- [x] Receber Pix Cash-in por QR Code dinâmico duedate.
- [x] Implementar bloqueio cautelar de recebimento Pix.
- [x] Consultar recebimentos Pix.
- [x] Devolver Pix Cash-in.
- [x] Implementar modelo de webhook Cash-in.

### Split Pix

- [x] Preparar split de Pix Cash-in por QR Code dinâmico duedate.
- [x] Preparar split de Pix Cash-in por QR Code dinâmico immediate.

### Cash-out

- [x] Implementar DICT.
- [x] Consultar chaves Pix externas DICT.
- [x] Realizar Pix Cash-out por agência e conta.
- [x] Realizar Pix Cash-out por chave Pix.
- [x] Realizar Pix Cash-out por QR Code estático.
- [x] Realizar Pix Cash-out por QR Code dinâmico.
- [x] Consultar transferências, pagamentos e devoluções de Pix-out.
- [x] Mapear tabela de erros Cash-out.
- [x] Implementar modelo de webhook Cash-out.
- [x] Implementar bloqueio cautelar de envio Pix.

### Gerenciamento de Chaves

- [x] Cadastrar chave Pix.
- [x] Excluir chaves Pix de uma conta.
- [x] Consultar chaves Pix de uma conta.
- [x] Alterar nome em uma chave Pix.

### Portabilidade e Reivindicação de Chaves

- [x] Documentar primeiros passos.
- [x] Solicitar portabilidade de chave Pix.
- [x] Responder portabilidade de chave Pix.
- [x] Consultar pedidos de portabilidade ou reivindicação.
- [x] Mapear tabela de erros de portabilidade Pix.

## Pix Automático

### Jornada Pagadora

- [x] Autorização - visão geral.
- [x] Autorização - jornada 1.
- [x] Autorização - jornada 2.
- [x] Autorização - jornada 3.
- [x] Autorização - jornada 4.
- [x] Agendamento.
- [x] Liquidação.
- [x] Cancelamento do consentimento.
- [x] Cancelamento de agendamento.
- [x] Consultas - visão pagador.

### Jornada Recebedora

- [x] Autorização - jornada 1.
- [x] Autorização - jornada 2.
- [x] Autorização - jornada 3.
- [x] Autorização - jornada 4.
- [x] Envio de agendamento.
- [x] Liquidação.
- [x] Retentativas de recebimento.
- [x] Cancelamento de agendamento.
- [x] Cancelamento de recorrência.
- [x] Motivos de rejeição pelo participante pagador.
- [x] FAQ Pix Automático (`docs/pix-automatico.md`).

## Pix Inteligente - Sweeping Accounts

- [x] Listar marcas do diretório de participantes.
- [x] Criar consentimento.
- [x] Callback do consentimento.
- [x] Cancelar consentimento.
- [x] Listar consentimentos.
- [x] Buscar informações de consentimento.
- [x] Criar Pix inteligente recorrente.

## Pix Indireto

- [x] Documentar sobre Pix Indireto (`docs/pix-indireto.md`).
- [x] Documentar pré-requisitos do participante.
- [x] Documentar fases de adesão do participante indireto.
- [x] Implementar DICT para participante indireto.
- [x] Implementar gestão de chaves Pix.
- [x] Implementar portabilidade e reivindicação.
- [x] Implementar infrações.
- [x] Implementar MED.

## Agendador de Transação Pix

- [x] Agendar transação Pix.
- [x] Consultar Pix agendado.
- [x] Cancelar Pix agendado.
- [x] Consultar lista de transações agendadas.

## Emissão de Boletos

- [x] Emitir boleto/cobrança.
- [x] Emitir boleto/cobrança com split de pagamento.
- [x] Criar PDF para cobrança.
- [x] Consultar boleto emitido.
- [x] Consultar boletos por período.
- [x] Cancelar boleto emitido.

## CNAB

- [x] Processar CNAB (`docs/cnab.md`).
- [x] Consultar CNAB.
- [x] Baixar CNAB.

## Open Finance as a Service

- [x] Listar marcas no diretório de participantes.
- [x] Obter detalhes de marcas.
- [x] Documentar uso e disponibilização de logotipos (`docs/open-finance.md`).
- [x] Documentar termos e condições de uso.
- [x] Implementar consentimento e consumo de dados.
- [x] Implementar API Resources.
- [x] Implementar API de dados cadastrais.
- [x] Implementar API cartões de crédito.
- [x] Implementar API contas.
- [x] Implementar API operações de crédito - empréstimos.
- [x] Implementar API operações de crédito - financiamento.
- [x] Implementar investimentos - renda fixa bancária.
- [x] Implementar investimentos - renda fixa crédito.
- [x] Implementar investimentos - renda variável.
- [x] Implementar investimentos - Tesouro Direto.
- [x] Implementar investimentos - fundos.
- [x] Mapear códigos de resposta e cenários de erro.

### Jornada Sem Redirecionamento

- [x] Documentar vínculo de dispositivo.
- [x] Implementar pagamento.
- [x] Criar vínculo.
- [x] Callback do vínculo.
- [x] FIDO Registration Options.
- [x] FIDO Registration.
- [x] Criar iniciação de pagamento v4.
- [x] FIDO Sign Options.
- [x] Autorização FIDO.
- [x] PIX v4.
- [~] Validação de biometria FIDO: o SDK valida o payload WebAuthn; a prova criptográfica e a biometria são executadas pelo dispositivo/navegador.

### Transferências Inteligentes - Sweeping Accounts

- [x] Criar payment initiation.
- [x] Executar pagamento Pix.
- [x] Consultas.
- [x] Cancelar consentimento.
- [x] Webhooks: recebimento, assinatura, deduplicação e persistência pelo endpoint comum de webhooks.
- [x] Máquina de estados.

### Pix Instantâneo - ITP

- [x] Criar consentimento Payment Initiation.
- [x] Máquina de estados Pix ITP.
- [x] Callback do consentimento.
- [x] Consultas: acompanhamento por callback, webhook e consulta de status na API.
- [x] Webhooks.
- [x] Códigos de resposta HTTP.
- [x] Possíveis erros de pagamento e classificação de retry.

### Pagamentos Automáticos Open Finance

- [x] Criar consentimento recorrente.
- [x] Jornada de autorização.
- [x] Callback e execução de pagamento Pix.
- [x] Retentativas intradia e extradia.
- [x] Consulta e cancelamento.
- [x] Webhooks Pix Automático.
- [x] Máquina de estados.

### Massa de Testes Open Finance

- [ ] Sucesso no valor máximo permitido no vínculo Pix.
- [ ] Sucesso no valor máximo permitido no vínculo Pix e limite diário.
- [ ] Falha por exceder limite.
- [ ] Falha por saldo insuficiente.
- [ ] Falha com múltiplas condições e precedência de erro.
- [ ] Falha por exceder limite diário.
- [ ] Falha por exceder limite noturno.
- [ ] Falha com consentimento expirado.
- [ ] Falha com consentimento revogado.
- [ ] Falha com vínculo rejeitado por timeout.

## Subadquirência e AaaS

- [x] Consultar status do credenciamento da conta.
- [x] Criar cliente.
- [x] Listar clientes.
- [x] Editar cliente.
- [x] Excluir cliente.
- [x] Criar cartão.
- [x] Listar cartões.
- [x] Inativar cartão.
- [x] Criar cobrança avulsa no cartão.
- [x] Listar cobranças avulsas.
- [x] Editar cobrança avulsa.
- [x] Retentar cobrança avulsa.
- [x] Estornar cobrança avulsa.
- [x] Cancelar cobrança avulsa.
- [x] Capturar cobrança no cartão.
- [x] Solicitar relatório de recebíveis.
- [x] Verificar status do relatório de recebíveis.
- [x] Visualizar relatório de recebíveis.
- [~] Criar plano.
- [~] Listar planos.
- [~] Editar plano.
- [~] Excluir plano.
- [~] Criar assinatura com ou sem plano.
- [~] Criar assinatura/contrato manual.
- [~] Listar assinaturas/contratos.
- [~] Adicionar transação.
- [~] Editar informações da assinatura/contrato.
- [~] Editar pagamento da assinatura/contrato.
- [~] Editar transação.
- [~] Retentar cobrança no cartão.
- [~] Capturar cobrança no cartão.
- [~] Estornar cobrança no cartão.
- [~] Cancelar assinatura/contrato.
- [~] Cancelar transação.
- [~] Listar chargebacks.
- [~] Enviar documentação de defesa de chargeback.
- [~] Desistir da disputa de chargeback.
- [~] Cadastrar webhooks de chargeback.
- [~] Simular fluxo chargeback sandbox.
- [~] Implementar tokenização de cartão via JS.
- [~] Listar taxas.
- [~] Listar transações.
- [~] Implementar extrato de recebíveis.

## Pagamento de Boletos

- [x] Realizar pagamento de boleto.
- [x] Consultar status de pagamento de boleto.
- [ ] Mapear tabela de erros de pagamento de contas.

## Transferência Entre Contas

- [x] Realizar transferência entre contas.
- [x] Consultar status de transferência entre contas.
- [ ] Mapear tabela de erros de transferência entre contas BaaS.

## TED

- [x] Receber transferências TED na conta BaaS.
- [x] Realizar transferência via TED.
- [x] Consultar status de transferência TED.
- [x] Implementar modelos de webhooks TED.

## Débito Veicular

- [x] Consultar débitos veiculares.
- [x] Efetivar pagamento de débitos veiculares.
- [x] Implementar débitos dependentes, distintos e obrigatórios.
- [ ] Criar massa de testes de débito veicular.
- [ ] Documentar FAQs.

## Recargas

- [ ] Consultar operadoras e valores.
- [ ] Realizar recarga.

## SLC

- [ ] Implementar recebimento de liquidações do arranjo de cartões via SLC.

## Informe de Rendimentos

- [x] Informe de rendimentos PF.
- [x] Informe de rendimentos PJ.

## Webhooks BaaS

- [x] Criar base de recebimento de webhooks.
- [x] Persistir eventos de webhook.
- [x] Deduplicar eventos de webhook.
- [x] Reprocessar webhook por endpoint administrativo.
- [ ] Cadastrar e gerenciar webhooks na Celcoin.
- [ ] Reenvio de webhook na Celcoin.
- [ ] Templates de webhooks BaaS.
- [ ] Bloqueio/desbloqueio de saldo por infração.

## Tabela de Erros BaaS

- [ ] Mapear erros por módulo.
- [ ] Criar exceções específicas por código remoto.
- [ ] Criar testes de mapeamento de erro.

## MED 2.0

- [ ] Criar recuperação de valores.
- [ ] Consultar recuperação de valores.
- [ ] Cancelar recuperação de valores.
- [ ] Receber recuperação de valores.
- [ ] Bloquear valores.
- [ ] Desbloquear valores.
- [ ] Devolver valores.
- [ ] Implementar webhooks MED 2.0.

## Core Banking - Bloqueio e Desbloqueio de Saldo

- [ ] Bloquear saldo.
- [ ] Desbloquear saldo.
- [ ] Criar cenários práticos.

## cel_banking - cards

- [ ] Documentar sobre cel_card.
- [x] Criar conta cartão.
- [x] Consultar dados de conta cartão.
- [x] Consultar limites da conta cartão.
- [~] Atualizar dados cadastrais.
- [~] Atualizar telefone.
- [x] Cancelar conta e cartão.
- [~] Implementar endereços.
- [x] Emitir cartão.
- [x] Emitir segunda via de cartão.
- [~] Consultar rastreio.
- [~] Simular rastreio.
- [x] Ativar cartão.
- [~] Alterar status.
- [x] Visualizar dados do cartão.
- [~] Listar cartões.
- [~] Alterar senha Pin Online e Pin Offline.
- [~] Implementar simulador de transações.
- [x] Cadastrar e gerenciar webhooks de cartão.
- [x] Template de webhooks de cartão.
- [~] Reenvio de webhook pendente.
- [~] Consultar dados de fatura pós-paga.

## cel_credit

- [x] Implementar autenticação de crédito.
- [x] Implementar variáveis personalizadas.
- [x] Campos de solicitação.
- [x] Simulações.
- [x] Status de solicitação.
- [ ] Tabela de erros de geração de boleto.
- [ ] Assinatura CCB por modalidade.
- [x] Assinatura via cláusula mandato timestamp.
- [ ] Assinatura via envio de PDF.
- [x] Consulta de assinaturas da CCB.
- [ ] Webhooks de crédito.

### Consignado Crédito Trabalhador

- [ ] Solicitação de proposta e distribuição aos originadores.
- [ ] Oferta, leilão, devolutiva e emissão.
- [ ] Solicitação e consentimento do tomador.
- [ ] Consulta de margem e simulação de crédito.
- [ ] Consulta de saldo FGTS e multa rescisória FGTS.
- [ ] Cadastro do tomador, emissão e cancelamento.
- [ ] Guia rápido consignado.
- [ ] Falhas de averbação Dataprev.
- [ ] Status da operação.
- [ ] Consultas, escriturações e repasses.
- [ ] Consulta status da garantia.

### Empréstimo Consignado Servidores do Exército

- [ ] Autenticação.
- [ ] Consulta de margem.
- [ ] Simulação da CCB.
- [ ] Criação do tomador.
- [ ] Compra com troco.
- [ ] Status da operação.

### Conta Escrow

- [ ] Onboarding conta escrow.
- [ ] Fluxo essencial.
- [ ] Depósitos.
- [ ] Destinatários e cobranças.
- [ ] Permissões.

### Portabilidade de Crédito

- [x] Portabilidade no ambiente do originador.
- [x] Simulação de portabilidade de crédito do trabalhador.
- [x] Autorização de consulta e consulta de vínculo empregatício.
- [x] Cadastro do tomador e emissão da CCB de portabilidade.
- [x] Solicitação do bundle/proposta de portabilidade.
- [~] Averbação por portabilidade e envio de contrato: executados no fluxo Celcoin/Dataprev após a solicitação; sem endpoint público adicional no contrato consultado.

## cel_banking - embedded solutions

- [ ] Conta Celcoin.
- [ ] DDA.
- [ ] Tabela de erros DDA.
- [ ] FAQs DDA.
- [ ] Débitos veiculares embedded.
- [ ] Enriquecimento de placas.
- [ ] Webhook com autenticação JWT.
- [ ] Idempotência de débito veicular.
- [ ] NFS-e.
- [ ] Tabela de erros NFS-e.
- [ ] Boletos conta única direto.
- [ ] Gestão de beneficiários.
- [ ] Gestão de carteiras.
- [ ] Gestão de emissão de boletos.
- [ ] Gestão de baixa de boletos.
- [ ] Pagamento de contas embedded.
- [ ] Devolução de pagamento de conta.
- [ ] Massa de dados para testes.
- [ ] Lista de convênios.
- [ ] Pix avulso embedded.
- [ ] Recargas nacionais.
- [ ] Saques e depósitos físicos.
- [ ] TED embedded.
- [ ] Conciliação.
- [ ] SLC Celcoin como banco liquidante.

## Pix Indireto - Produto Dedicado

- [ ] Listar todas as chaves Pix de cliente.
- [ ] Portabilidade e reivindicação.
- [ ] Infrações.
- [ ] MED.
- [ ] Marcação de fraude.
- [ ] Criação de recuperação de valores.
- [ ] Consulta de recuperação de valores.
- [ ] Cancelamento de recuperação de valores.
- [ ] Solicitação de devolução em recuperação de valores.
- [ ] Consulta de grafo de recuperação de valores.
- [ ] Atualização de recuperação de valores.
- [ ] Status da recuperação de valores.
- [ ] Fechar recuperação de valores.
- [ ] Fechar devolução em recuperação de valores.
- [ ] Autorização do Cash-in.
- [ ] QR Code Pix Indireto.
- [ ] Pagamentos Pix cash-out para indiretos.
- [ ] Recebimentos Pix cash-in.
- [ ] Devolução de recebimentos Pix.
- [ ] Reporte Bacen de transações fora do SPI.
- [ ] Pix Saque/Troco.
- [ ] Tabela de erros para participantes indiretos.
- [ ] Webhooks do participante indireto.

## CEL_BRICKS WEBHOOKS

- [ ] Webhook Manager.
- [ ] Cadastrar e gerenciar webhooks.
- [ ] Reenvio de webhook.

## cel_open

### Compartilhamento de Dados

- [ ] Consentimento.
- [ ] Direcionamento.
- [ ] Autenticação.
- [ ] Confirmação.
- [ ] Redirecionamento.
- [ ] Documentação técnica transmissora.
- [ ] Documentação técnica receptora.

### Jornada Com Redirecionamento

- [ ] Criar iniciação de pagamento v4.
- [ ] Callback da iniciação de pagamento.
- [ ] PIX v4.
- [ ] Criar jornada de pagamento v4.
- [ ] Listar jornadas de pagamento v4.
- [ ] Buscar jornada de pagamento v4.

### Jornada Sem Redirecionamento

- [x] Criar vínculo.
- [x] Pagamento.
- [x] Callback do vínculo.
- [x] FIDO Registration Options.
- [x] FIDO Registration.
- [x] Criar iniciação de pagamento v4.
- [x] FIDO Sign Options.
- [x] Autorização FIDO.
- [x] PIX v4.
- [x] Criar jornada de vínculo.
- [~] Listar jornadas de vínculo: endpoint de listagem não publicado no contrato consultado.
- [~] Buscar jornada de vínculo: endpoint de consulta não publicado no contrato consultado.
- [x] Criar jornada de pagamento v4.
- [~] Listar jornadas de pagamento v4: endpoint de listagem não publicado no contrato consultado.
- [~] Buscar jornada de pagamento v4: endpoint de consulta não publicado no contrato consultado.

### Brick Bank e Brick Insurance

- [ ] Brick Bank receptora de dados.
- [ ] Brick Bank transmissora de dados.
- [ ] Brick Bank detentora de contas.
- [ ] Brick Insurance receptora de dados.
- [ ] Brick Insurance transmissora de dados.

### Open Plus

- [ ] Portal: cadastro e acesso.
- [ ] Hall Open Plus.
- [ ] Gestão de aplicações.
- [ ] Catálogo de produtos.
- [ ] Gestão de membros da equipe.
- [ ] Menu de usuário.
- [ ] Painel de gestão da aplicação.
- [ ] Painel de gestão do produto.
- [ ] Documentação de produto.
- [ ] Credenciais e usuário de teste.
- [ ] Subir em produção.
- [ ] Portal configurações.

### Plataforma de Iniciação ITP

- [ ] Dashboard Open Keys.
- [ ] Lista de transações.
- [ ] Lista de vínculos de conta.
- [ ] Relatórios.
- [ ] Configurações da jornada de pagamentos.
- [ ] Demo das jornadas de pagamento.
- [ ] Demo da área de gestão.
- [ ] Iniciação de pagamento instantâneo.
- [ ] Gestão de pagamentos instantâneos.
- [ ] Iniciação de pagamento agendado.
- [ ] Gestão de pagamentos agendados.
- [ ] Iniciação de pagamento sem redirecionamento.
- [ ] Gestão de contas salvas.
- [ ] Iniciação de pagamento automático.
- [ ] Gestão de pagamentos automáticos.
- [ ] FAQ Open Keys.
- [ ] Jornada Pay by Link.
- [ ] Pix por aproximação.
- [ ] Máquina de estados.
- [ ] Códigos de resposta HTTP.
- [ ] Possíveis erros de pagamentos.
- [ ] Diagrama de sequência com redirecionamento.
- [ ] Diagrama de sequência sem redirecionamento.
- [ ] Webhook.
- [ ] APIs.
- [ ] Criar consentimento Sweeping Accounts.
- [ ] Callback de consentimento Sweeping Accounts.
- [ ] Cancelamento de consentimento Sweeping Accounts.
- [ ] Listar consentimentos.
- [ ] Buscar consentimento.
- [ ] Criação de sessão de jornada.
- [ ] Listar sessões de jornada.
- [ ] Buscar sessão de jornada.
- [ ] Pix inteligente.

## Pix - Avulso

- [ ] Documentar sobre Pix avulso.
- [ ] Cobrança imediata com QR Code dinâmico immediate.
- [ ] Cobrança com vencimento com QR Code dinâmico duedate.
- [ ] Cobrança estática.
- [ ] Consulta de recebimentos Pix.
- [ ] Devolução de recebimentos Pix.
- [ ] DICT e balde de fichas.
- [ ] Transferência Pix por dados bancários.
- [ ] Transferência Pix por chave.
- [ ] Pagamento de QR Code estático.
- [ ] Pagamento de QR Code dinâmico.
- [ ] Pix entre contas.
- [ ] Consultar transferências, pagamentos e devoluções Pix-out.
- [ ] Pagamento de QR Code estático ou dinâmico.
- [ ] Pix Automático pagador.
- [ ] Pix Automático recebedor.
- [ ] Tabela de erros Pix.
- [ ] FAQs Pix.
- [ ] Modelos de webhooks Pix.

## Conciliação

- [ ] API para conciliação.
- [ ] Documentar API de conciliação.
- [ ] API para extrato consolidado.
- [ ] Documentar API de extrato consolidado.
- [ ] Tabela de erros.

## Suporte

- [ ] Central de ajuda.
- [ ] SLA de atendimento.

## Painel do Cliente

- [ ] Listagem de tickets.

## Anti-fraudes

- [ ] Follow the Money.
- [ ] Regras antifraude por transação.
- [ ] Webhooks antifraude.
- [ ] Auditoria e retenção de eventos antifraude.

## Qualidade Transversal

- [x] Java 21.
- [x] Spring Boot 3.5.x.
- [x] WebClient.
- [x] Validation.
- [x] Actuator.
- [x] Spring Retry.
- [x] Resilience4j como dependência.
- [x] Jackson.
- [x] Caffeine Cache.
- [x] JUnit 5.
- [x] Mockito.
- [x] WireMock.
- [x] Testcontainers.
- [x] PostgreSQL.
- [x] Flyway.
- [x] Docker Compose.
- [x] Maven Enforcer.
- [x] JaCoCo.
- [x] Spotless configurado.
- [x] Aplicar Spotless em CI.
- [x] Elevar regra JaCoCo para 80% domínio e 100% autenticação.
- [x] Criar pipeline CI.
- [x] Publicar artefato SNAPSHOT.
