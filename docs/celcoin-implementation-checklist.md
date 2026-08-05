# Checklist de Implementação Celcoin

Legenda:

- `[x]` já existe no SDK atual.
- `[ ]` pendente.
- `[~]` estrutura preparada, aguardando contrato oficial de endpoint/payload.

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
- [ ] Implementar arquivo de movimentação via SFTP.
- [ ] Avaliar e implementar servidor MCP da Celcoin.

## Validação no Sandbox

Status da integração com o ambiente de sandbox (`https://sandbox.openfinance.celcoin.dev`):

- [x] Credenciais de sandbox configuradas em `application-local.yml` e `.env.example`.
- [x] Autenticação `POST /v5/token` validada (HTTP 200, token OAuth com validade de 2400s).
- [x] Primeira consulta funcional `GET /pix/v1/participants` validada (lista real de participantes do SPI).
- [x] Criação de QR Code dinâmico `POST /pix/v1/brcode/dynamic` validada (transactionId `4000715670`, EMV gerado).
- [x] Decodificação de EMV `POST /pix/v1/emv/full` validada (type/key/amount/transactionIdentification).
- [x] Cobrança estática `POST /pix/v1/brcode/static` validada (transactionId `40000046501`; `amount` deve ser numérico).
- [x] Autenticação `POST /v5/token` via `application/x-www-form-urlencoded` (SDK já usa form-urlencoded).
- [x] Suíte automatizada verde (`./mvnw clean verify`: 119 testes, 0 falhas, JaCoCo OK).
- [x] Validação das migrations Flyway via Testcontainers (PostgreSQL em Docker).

## Homologação

- [ ] Criar guia de homologação por produto.
- [ ] Criar massa de testes por ambiente.
- [ ] Criar checklist de evidências para aprovação.
- [ ] Criar coleção de requests para homologação.

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
- [ ] Implementar autenticação biométrica (produto separado da Celcoin; via `SELFIE` na proposta).
- [ ] Implementar validação de prova de vida (sem endpoint dedicado no contrato oficial; via webview).
- [x] Criar DTOs de KYC PF.
- [x] Criar DTOs de KYC PJ.
- [x] Criar DTOs de documentos e anexos.
- [x] Criar DTOs de resposta e status de análise.
- [x] Mapear webhooks de onboarding KYC.
- [ ] Mapear tabela de erros de onboarding.
- [x] Adicionar exemplos de uso em `docs/examples.md`.
- [x] Documentar fluxo funcional de onboarding KYC em `docs/onboarding.md`.
- [x] Implementar testes unitários de serialização dos DTOs.
- [x] Implementar testes WireMock de onboarding.

## cel_banking - BaaS & Core

### Sobre o BaaS & Core Banking

- [ ] Documentar visão geral do BaaS & Core Banking.
- [ ] Documentar FAQs.
- [ ] Documentar diretriz de Termos de Uso - BaaS.

### Follow the Money

- [ ] Mapear endpoints de antifraude e PLD.
- [ ] Implementar DTOs e interfaces.
- [ ] Implementar webhooks relacionados.

### APP e Internet Banking Whitelabel

- [ ] Documentar integração MyBenk.
- [ ] Mapear APIs disponíveis.

### Painel do Cliente

- [ ] Documentar recursos do painel do contratante.
- [ ] Mapear APIs administrativas disponíveis.

### Abertura de Contas KYC

- [~] Implementar criação de contas via onboarding.
- [~] Implementar informações financeiras.
- [~] Implementar webhooks de onboarding.
- [~] Implementar simulação de status de onboarding para sandbox.
- [ ] Implementar BC Protege+.
- [~] Criar cenários práticos de abertura de conta PJ.
- [~] Implementar onboarding sem WebView.
- [ ] Criar FAQs de abertura de contas.

### Gestão de Contas

- [~] Criar interface de gestão de contas.
- [~] Criar DTOs mínimos de conta.
- [~] Criar conta apenas Core Banking.
- [~] Implementar informações financeiras de conta.
- [~] Atualizar dados do cliente.
- [~] Desativar ou encerrar conta.
- [~] Listar contas.
- [ ] Mapear tabela de erros de gestão de contas.
- [~] Consultar número de contas.
- [~] Implementar bloqueios judiciais.
- [~] Implementar atualização de status de conta.
- [~] Adicionar saldo em sandbox.
- [~] Implementar monitoramento cadastral.
- [~] Implementar webhooks de monitoramento cadastral.
- [~] Implementar simulação de monitoramento cadastral.

### Relatórios

- [~] Consultar saldo.
- [~] Consultar saldo do dia e movimentações diárias consolidadas.
- [~] Consultar extrato.
- [~] Consultar extrato detalhado.
- [~] Consultar transações do extrato.
- [ ] Implementar paginação oficial dos relatórios.
- [ ] Implementar testes WireMock dos relatórios.

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

- [~] Autorização - visão geral.
- [~] Autorização - jornada 1.
- [~] Autorização - jornada 2.
- [~] Autorização - jornada 3.
- [~] Autorização - jornada 4.
- [~] Agendamento.
- [~] Liquidação.
- [~] Cancelamento do consentimento.
- [~] Cancelamento de agendamento.
- [~] Consultas - visão pagador.

### Jornada Recebedora

- [~] Autorização - jornada 1.
- [~] Autorização - jornada 2.
- [~] Autorização - jornada 3.
- [~] Autorização - jornada 4.
- [~] Envio de agendamento.
- [~] Liquidação.
- [~] Retentativas de recebimento.
- [~] Cancelamento de agendamento.
- [~] Cancelamento de recorrência.
- [~] Motivos de rejeição pelo participante pagador.
- [ ] FAQ Pix Automático.

## Pix Inteligente - Sweeping Accounts

- [ ] Listar marcas do diretório de participantes.
- [ ] Criar consentimento.
- [ ] Callback do consentimento.
- [ ] Cancelar consentimento.
- [ ] Listar consentimentos.
- [ ] Buscar informações de consentimento.
- [ ] Criar Pix inteligente recorrente.

## Pix Indireto

- [ ] Documentar sobre Pix Indireto.
- [ ] Documentar pré-requisitos do participante.
- [ ] Documentar fases de adesão do participante indireto.
- [ ] Implementar DICT para participante indireto.
- [ ] Implementar gestão de chaves Pix.
- [ ] Implementar portabilidade e reivindicação.
- [ ] Implementar infrações.
- [ ] Implementar MED.

## Agendador de Transação Pix

- [x] Agendar transação Pix.
- [x] Consultar Pix agendado.
- [x] Cancelar Pix agendado.
- [x] Consultar lista de transações agendadas.

## Emissão de Boletos

- [~] Emitir boleto/cobrança.
- [ ] Emitir boleto/cobrança com split de pagamento.
- [~] Criar PDF para cobrança.
- [~] Consultar boleto emitido.
- [~] Consultar boletos por período.
- [~] Cancelar boleto emitido.

## CNAB

- [ ] Processar CNAB.
- [ ] Consultar CNAB.
- [ ] Baixar CNAB.

## Open Finance as a Service

- [ ] Listar marcas no diretório de participantes.
- [ ] Obter detalhes de marcas.
- [ ] Documentar uso e disponibilização de logotipos.
- [ ] Documentar termos e condições de uso.
- [ ] Implementar consentimento e consumo de dados.
- [ ] Implementar API Resources.
- [ ] Implementar API de dados cadastrais.
- [ ] Implementar API cartões de crédito.
- [ ] Implementar API contas.
- [ ] Implementar API operações de crédito - empréstimos.
- [ ] Implementar API operações de crédito - financiamento.
- [ ] Implementar investimentos - renda fixa bancária.
- [ ] Implementar investimentos - renda fixa crédito.
- [ ] Implementar investimentos - renda variável.
- [ ] Implementar investimentos - Tesouro Direto.
- [ ] Implementar investimentos - fundos.
- [ ] Mapear códigos de resposta e cenários de erro.

### Jornada Sem Redirecionamento

- [ ] Documentar vínculo de dispositivo.
- [ ] Implementar pagamento.
- [ ] Criar vínculo.
- [ ] Callback do vínculo.
- [ ] FIDO Registration Options.
- [ ] FIDO Registration.
- [ ] Criar iniciação de pagamento v4.
- [ ] FIDO Sign Options.
- [ ] Autorização FIDO.
- [ ] PIX v4.
- [ ] Validação de biometria FIDO.

### Transferências Inteligentes - Sweeping Accounts

- [ ] Criar payment initiation.
- [ ] Executar pagamento Pix.
- [ ] Consultas.
- [ ] Cancelar consentimento.
- [ ] Webhooks.
- [ ] Máquina de estados.

### Pix Instantâneo - ITP

- [ ] Criar consentimento Payment Initiation.
- [ ] Máquina de estados Pix ITP.
- [ ] Callback do consentimento.
- [ ] Consultas.
- [ ] Webhooks.
- [ ] Códigos de resposta HTTP.
- [ ] Possíveis erros de pagamento.

### Pagamentos Automáticos Open Finance

- [ ] Criar consentimento recorrente.
- [ ] Jornada de autorização.
- [ ] Callback e execução de pagamento Pix.
- [ ] Retentativas intradia e extradia.
- [ ] Consulta e cancelamento.
- [ ] Webhooks Pix Automático.
- [ ] Máquina de estados.

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

- [~] Consultar status do credenciamento da conta.
- [~] Criar cliente.
- [~] Listar clientes.
- [~] Editar cliente.
- [~] Excluir cliente.
- [~] Criar cartão.
- [~] Listar cartões.
- [~] Inativar cartão.
- [~] Criar cobrança avulsa no cartão.
- [~] Listar cobranças avulsas.
- [~] Editar cobrança avulsa.
- [~] Retentar cobrança avulsa.
- [~] Estornar cobrança avulsa.
- [~] Cancelar cobrança avulsa.
- [~] Capturar cobrança no cartão.
- [~] Solicitar relatório de recebíveis.
- [~] Verificar status do relatório de recebíveis.
- [~] Visualizar relatório de recebíveis.
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

- [ ] Realizar pagamento de boleto.
- [ ] Consultar status de pagamento de boleto.
- [ ] Mapear tabela de erros de pagamento de contas.

## Transferência Entre Contas

- [~] Realizar transferência entre contas.
- [~] Consultar status de transferência entre contas.
- [ ] Mapear tabela de erros de transferência entre contas BaaS.

## TED

- [ ] Receber transferências TED na conta BaaS.
- [ ] Realizar transferência via TED.
- [ ] Consultar status de transferência TED.
- [ ] Implementar modelos de webhooks TED.

## Débito Veicular

- [ ] Consultar débitos veiculares.
- [ ] Efetivar pagamento de débitos veiculares.
- [ ] Implementar débitos dependentes, distintos e obrigatórios.
- [ ] Criar massa de testes de débito veicular.
- [ ] Documentar FAQs.

## Recargas

- [ ] Consultar operadoras e valores.
- [ ] Realizar recarga.

## SLC

- [ ] Implementar recebimento de liquidações do arranjo de cartões via SLC.

## Informe de Rendimentos

- [ ] Informe de rendimentos PF.
- [ ] Informe de rendimentos PJ.

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
- [~] Criar conta cartão.
- [~] Consultar dados de conta cartão.
- [~] Consultar limites da conta cartão.
- [~] Atualizar dados cadastrais.
- [~] Atualizar telefone.
- [~] Cancelar conta e cartão.
- [~] Implementar endereços.
- [~] Emitir cartão.
- [~] Emitir segunda via de cartão.
- [~] Consultar rastreio.
- [~] Simular rastreio.
- [~] Ativar cartão.
- [~] Alterar status.
- [~] Visualizar dados do cartão.
- [~] Listar cartões.
- [~] Alterar senha Pin Online e Pin Offline.
- [~] Implementar simulador de transações.
- [~] Cadastrar e gerenciar webhooks de cartão.
- [~] Template de webhooks de cartão.
- [~] Reenvio de webhook pendente.
- [~] Consultar dados de fatura pós-paga.

## cel_credit

- [ ] Implementar autenticação de crédito.
- [ ] Implementar variáveis personalizadas.
- [ ] Campos de solicitação.
- [ ] Simulações.
- [ ] Status de solicitação.
- [ ] Tabela de erros de geração de boleto.
- [ ] Assinatura CCB por modalidade.
- [ ] Assinatura via cláusula mandato timestamp.
- [ ] Assinatura via envio de PDF.
- [ ] Consulta de assinaturas da CCB.
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

- [ ] Portabilidade no ambiente do originador.
- [ ] Simulação de portabilidade de crédito do trabalhador.
- [ ] Autorização de consulta e consulta de vínculo empregatício.
- [ ] Cadastro do tomador e emissão da CCB de portabilidade.
- [ ] Proposta de portabilidade e resposta.
- [ ] Averbação por portabilidade e envio de contrato.

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

- [ ] Criar vínculo.
- [ ] Pagamento.
- [ ] Callback do vínculo.
- [ ] FIDO Registration Options.
- [ ] FIDO Registration.
- [ ] Criar iniciação de pagamento v4.
- [ ] FIDO Sign Options.
- [ ] Autorização FIDO.
- [ ] PIX v4.
- [ ] Criar jornada de vínculo.
- [ ] Listar jornadas de vínculo.
- [ ] Buscar jornada de vínculo.
- [ ] Criar jornada de pagamento v4.
- [ ] Listar jornadas de pagamento v4.
- [ ] Buscar jornada de pagamento v4.

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
