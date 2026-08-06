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
- [x] Avaliar e implementar servidor MCP: adaptador JSON-RPC MCP opt-in em `/mcp`, com `initialize`, `tools/list` e `tools/call` para participantes Pix, decodificação EMV e saldo; não é um servidor oficial Celcoin.

## Frontend React Native — cobertura de produto

Esta seção representa o trabalho necessário para entregar um aplicativo
React Native de produção consumindo o SDK por uma API BFF. O aplicativo não
deve conter `clientSecret`, certificado mTLS, credenciais SFTP ou regras
antifraude; essas responsabilidades permanecem no backend. A arquitetura e
os contratos móveis estão descritos em `docs/react-native.md`.

### Fundação do aplicativo

- [x] Criar workspace React Native CLI com TypeScript e configuração Android/iOS.
- [x] Habilitar a mesma base para web com `react-native-web` e Vite, sem Expo.
- [x] Definir baseline React Native 0.86, Node 22.13+, Java/Android SDK/Xcode no README do frontend.
- [x] Configurar lint, formatter, typecheck e testes.
- [x] Configurar hooks de commit e validação local obrigatória (`frontend/.husky/pre-commit` com `lint-staged`).
- [x] Configurar ambientes `development`, `staging`, `sandbox` e `production` via `react-native-config`.
- [x] Implementar configuração por ambiente sem segredos no bundle.
- [ ] Criar API BFF versionada para o app, com correlation ID e idempotência.
- [x] Criar cliente-base TypeScript para a API BFF versionada.
- [ ] Gerar cliente TypeScript a partir do contrato OpenAPI do BFF.
- [x] Implementar cliente HTTP com timeout, retry inicial, cancelamento e tratamento de erros.
- [x] Implementar cache base e invalidação via TanStack Query.
- [x] Implementar estado global, estado de sessão e base para estado de formulários via Zustand.
- [x] Implementar navegação autenticada, pública e fluxo de sessão expirada.
- [x] Implementar armazenamento seguro de tokens com Keychain/Keystore via `react-native-keychain`.
- [x] Implementar logout local, revogação de sessão e limpeza de dados sensíveis.
- [x] Implementar deep links, universal links e app links na configuração de navegação.
- [x] Implementar feature flags por ambiente e produto contratado.
- [x] Implementar telemetria sem CPF, CNPJ, token, cartão ou certificado em logs.

### Segurança, identidade e onboarding

- [x] Implementar login, recuperação de acesso, troca de senha e MFA conforme o BFF (`core/identity/identity-api.ts`).
- [x] Implementar aceite de Termos de Uso, Política de Privacidade e consentimentos versionados (`core/identity/consents.ts`).
- [x] Implementar cadastro PF com validação de CPF, telefone, e-mail e endereço (`screens/OnboardingScreen.tsx`).
- [x] Implementar cadastro PJ com CNPJ, representantes, sócios e endereço (`core/onboarding/registration-api.ts`).
- [ ] Implementar captura de documentos com câmera, galeria e compressão segura.
- [x] Implementar upload multipart para o BFF com progresso, retry e base para retomada (`core/onboarding/document-upload.ts`).
- [x] Implementar fluxo público de onboarding HTTPS quando exigido pelo produto (`core/onboarding/public-flow.ts`).
- [x] Implementar disponibilidade/preferência de autenticação biométrica com fallback seguro (`core/identity/biometrics.ts`).
- [ ] Implementar prova de vida e tratamento dos estados `BIOMETRIC_LIVENESS` e `BIOMETRIC_DOC_LIVENESS`.
- [x] Implementar contrato de consulta de status KYC com estados de pendência, aprovação, recusa e biometria (`core/onboarding/onboarding-api.ts`).
- [x] Implementar bloqueio de captura por permissão negada, dispositivo comprometido ou sessão expirada (`core/onboarding/capture-policy.ts`).
- [x] Implementar proteção contra screenshot no Android via `FLAG_SECURE`.

### Conta, saldo e movimentações

- [x] Implementar dashboard de conta, saldo disponível e saldo bloqueado.
- [x] Implementar extrato paginado, filtros por data/tipo/status e detalhe de transação.
- [x] Implementar consulta de movimentações do dia e atualização por pull-to-refresh.
- [x] Implementar dados cadastrais, informações financeiras e edição de perfil.
- [x] Implementar troca de conta ativa quando o usuário possuir mais de uma conta.
- [x] Implementar encerramento/desativação com confirmação e motivo no contrato BFF.
- [x] Implementar estados de conta: ativa, bloqueada, encerrada, pendente e sob análise.
- [x] Implementar bloqueios judiciais apenas como consulta/status; a decisão permanece no backend.
- [x] Implementar contrato de informe de rendimentos e download HTTPS seguro.
- [x] Implementar contratos de transferências entre contas, TED e consulta de status.
- [x] Implementar contratos de confirmação/status e comprovante via URL HTTPS, sem expor tokens no app.

### Pix

- [ ] Implementar leitura de QR Code Pix com câmera; [x] validação local do payload EMV (`core/pix/pix-emv.ts`).
- [x] Implementar pagamento Pix por chave, dados bancários, QR estático e QR dinâmico (`core/pix/pix-api.ts`).
- [x] Implementar criação e exibição de cobranças Pix immediate, duedate e estática (`core/pix/pix-api.ts`).
- [x] Implementar Pix entre contas e confirmação de favorecido no contrato BFF.
- [x] Implementar consulta de pagamentos, recebimentos, devoluções e bloqueios cautelares.
- [x] Implementar cadastro, consulta, alteração e exclusão de chaves Pix.
- [x] Implementar portabilidade e reivindicação de chaves com timeline de status.
- [ ] Implementar Pix Automático pagador e recebedor.
- [ ] Implementar Pix Indireto, Pix Avulso e Pix Inteligente conforme feature flag/contrato.
- [ ] Implementar Pix Saque/Troco quando habilitado para o parceiro.
- [x] Implementar tela/contrato de comprovante Pix por URL HTTPS segura.
- [x] Implementar prevenção de duplicidade no app e idempotência nas mutações encaminhadas ao BFF.

### Boletos, recargas e débitos

- [x] Implementar pagamento de boleto por linha digitável e código de barras (`core/payments/payments-api.ts`).
- [x] Implementar autorização, pagamento, consulta, cancelamento e comprovante de boleto.
- [x] Implementar emissão e listagem de boletos quando o produto estiver habilitado.
- [x] Implementar recarga de celular com seleção de operadora, produto e valor.
- [x] Implementar consulta de status e reprocessamento seguro de recarga com idempotência estável.
- [x] Implementar consulta e pagamento de débitos veiculares.
- [x] Implementar seleção de débitos, validação de valor e comprovante veicular.
- [x] Implementar tratamento de vencimento, indisponibilidade e pagamento em análise.

### Cartões, crédito e escrow

- [x] Implementar onboarding de cartão, cartões virtuais e cartões físicos conforme contrato (`core/financial/cards-api.ts`).
- [x] Implementar ativação, bloqueio/desbloqueio, limite, fatura e transações do cartão.
- [x] Implementar mascaramento de PAN/CVV e proibir armazenamento de dados de cartão no app.
- [x] Implementar proposta de crédito, simulação, documentos e acompanhamento de análise (`core/financial/credit-api.ts`).
- [x] Implementar consignado Crédito Trabalhador e consignado de servidores quando habilitados.
- [x] Implementar portabilidade de crédito e acompanhamento da solicitação.
- [x] Implementar conta Escrow com saldo, eventos, partes e estados autorizados (`core/financial/escrow-api.ts`).
- [x] Implementar mensagens de elegibilidade e indisponibilidade por contrato/produto.

### Open Finance, ITP e jornadas

- [x] Implementar seleção de instituição e consentimento Open Finance (`core/open-finance/open-finance-api.ts`).
- [x] Implementar jornada com redirecionamento via deep link/Universal Link (`core/open-finance/redirect-flow.ts`).
- [x] Implementar adaptador de jornada sem redirecionamento com FIDO2/WebAuthn por módulo nativo.
- [x] Implementar registro de passkey, autenticação, cancelamento e fallback (`core/open-finance/passkey.ts`).
- [x] Implementar iniciação de pagamentos imediatos, agendados e automáticos.
- [x] Implementar Sweeping Accounts e Transferências Inteligentes via contrato BFF.
- [x] Implementar Brick Bank/Insurance somente atrás dos contratos habilitados.
- [x] Implementar tela de consentimentos, instituições, vínculos, pagamentos e revogações.
- [x] Implementar estados de callback, timeout, recusa, expiração e consentimento duplicado.

### Notificações, suporte e operação

- [x] Implementar registro e rotação de device token para push Android/iOS (`core/operations/operations-api.ts`).
- [x] Processar eventos push somente após validação no backend; payload nunca altera saldo (`core/operations/push-handler.ts`).
- [x] Implementar central de notificações e marcação de leitura.
- [x] Implementar suporte, FAQ, abertura e acompanhamento de tickets via BFF quando contratado.
- [x] Implementar tela de indisponibilidade, manutenção e status de serviços.
- [x] Implementar atendimento de erro com código Celcoin e mensagem segura para o usuário.

### Qualidade, segurança e publicação

- [x] Criar testes unitários de reducers, hooks, validadores e formatadores.
- [x] Criar testes de componentes e fluxos críticos com React Native Testing Library.
- [~] Criar e executar testes E2E Android/iOS para todos os fluxos habilitados; matriz e critérios estão em `frontend/e2e/test-matrix.md`, mas a execução exige simuladores/dispositivos e BFF de staging.
- [x] Executar primeira fatia E2E web com Playwright: login aceito, dashboard, Pix e login recusado.
- [x] Criar matriz consolidada de cobertura E2E por feature em `docs/frontend-e2e-coverage.md`.
- [x] Criar testes de contrato do BFF e mocks versionados por ambiente (`frontend/test/fixtures`).
- [x] Criar base de testes de acessibilidade para VoiceOver/TalkBack, fonte ampliada e contraste.
- [x] Cobrir timeout, retry, duplicidade e base para retomada de upload; cenários offline/rede lenta ficam no E2E.
- [ ] Testar jailbreak/root, debugger, certificado inválido e armazenamento comprometido.
- [x] Configurar base de observabilidade com mascaramento de dados pessoais (`core/security/redaction.ts`).
- [ ] Configurar CI/CD para Android e iOS com assinatura fora do repositório.
- [ ] Configurar distribuição interna, TestFlight, Play Internal Testing e homologação.
- [ ] Executar pentest mobile, revisão de permissões e análise de dependências.
- [ ] Publicar política de privacidade, fichas de segurança e evidências de homologação.
- [x] Criar checklist de release, rollback, migração de storage e compatibilidade de versões (`docs/mobile-release-checklist.md`).
- [ ] Obter aprovação final de App Store e Google Play e validar produção com feature flags.

### Definição de 100% do frontend

- [~] Fluxos habilitados possuem contrato de estados reutilizável (`components/AsyncState.tsx`); a cobertura visual final depende de cada produto contratado.
- [x] Operações financeiras usam BFF e idempotência; confirmações e comprovantes são contratos obrigatórios dos módulos.
- [x] Nenhum segredo Celcoin ou dado completo de cartão é empacotado no aplicativo; há secret scan na CI.
- [x] Callbacks e webhooks são processados no backend e refletidos por consulta/push seguro (`core/operations/push-handler.ts`).
- [~] Testes unitários, de contrato e a matriz E2E estão implementados; execução em staging/sandbox é gate operacional.
- [~] Evidências e aprovação de segurança, acessibilidade, homologação e publicação dependem da execução externa.

## Validação no Sandbox

Status da integração com o ambiente de sandbox (`https://sandbox.openfinance.celcoin.dev`):

- [x] Credenciais de sandbox configuradas em `application-local.yml` e `.env.example`.
- [x] Autenticação `POST /v5/token` validada (HTTP 200, token OAuth com validade de 2400s).
- [x] Primeira consulta funcional `GET /pix/v1/participants` validada (lista real de participantes do SPI).
- [x] Criação de QR Code dinâmico `POST /pix/v1/brcode/dynamic` validada (transactionId `4000715670`, EMV gerado).
- [x] Decodificação de EMV `POST /pix/v1/emv/full` validada (type/key/amount/transactionIdentification).
- [x] Cobrança estática `POST /pix/v1/brcode/static` validada (transactionId `40000046501`; `amount` deve ser numérico).
- [x] Autenticação `POST /v5/token` via `application/x-www-form-urlencoded` (SDK já usa form-urlencoded).
- [x] Suíte automatizada: `./mvnw verify` passa com 145 testes, 0 falhas, 0 erros e cobertura JaCoCo global de 68,49%.
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
- [x] Implementar autenticação biométrica: criação, consulta, documentos e webhook do produto dedicado.
- [x] Implementar validação de prova de vida: fluxo `BIOMETRIC_LIVENESS`/`BIOMETRIC_DOC_LIVENESS` via WebView biométrica.
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

- [x] Mapear Follow the Money: avaliação de risco Pix por CPF/CNPJ implementada; configuração de regras continua contratual.
- [x] Implementar DTOs e interfaces neutros de decisão/FtM no receptor de webhooks; regras remotas dependem de contrato.
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
- [x] Implementar webhooks de monitoramento cadastral pelo receptor comum de webhooks.
- [x] Implementar simulação de monitoramento cadastral.

### Relatórios

- [x] Consultar saldo.
- [x] Consultar saldo do dia e movimentações diárias consolidadas.
- [x] Consultar extrato.
- [x] Consultar extrato detalhado.
- [x] Consultar transações do extrato.
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
- [x] Validação de biometria FIDO: o SDK valida o payload WebAuthn; a prova criptográfica e a biometria são executadas pelo dispositivo/navegador.

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

- [x] Sucesso no valor máximo permitido no vínculo Pix.
- [x] Sucesso no valor máximo permitido no vínculo Pix e limite diário.
- [x] Falha por exceder limite.
- [x] Falha por saldo insuficiente.
- [x] Falha com múltiplas condições e precedência de erro.
- [x] Falha por exceder limite diário.
- [x] Falha por exceder limite noturno.
- [x] Falha com consentimento expirado.
- [x] Falha com consentimento revogado.
- [x] Falha com vínculo rejeitado por timeout.

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
- [x] Criar plano.
- [x] Listar planos.
- [x] Editar plano.
- [x] Excluir plano.
- [x] Criar assinatura com ou sem plano.
- [x] Criar assinatura/contrato manual.
- [x] Listar assinaturas/contratos.
- [x] Adicionar transação.
- [x] Editar informações da assinatura/contrato.
- [x] Editar pagamento da assinatura/contrato.
- [x] Editar transação.
- [x] Retentar cobrança no cartão.
- [x] Capturar cobrança no cartão.
- [x] Estornar cobrança no cartão.
- [x] Cancelar assinatura/contrato.
- [x] Cancelar transação.
- [x] Listar chargebacks.
- [x] Enviar documentação de defesa de chargeback.
- [x] Desistir da disputa de chargeback.
- [x] Cadastrar webhooks de chargeback.
- [x] Simular fluxo chargeback sandbox.
- [x] Implementar tokenização de cartão via JS.
- [x] Listar taxas.
- [x] Listar transações.
- [x] Implementar extrato de recebíveis.

## Pagamento de Boletos

- [x] Realizar pagamento de boleto.
- [x] Consultar status de pagamento de boleto.
- [x] Mapear tabela de erros de pagamento de contas.

## Transferência Entre Contas

- [x] Realizar transferência entre contas.
- [x] Consultar status de transferência entre contas.
- [x] Mapear tabela de erros de transferência entre contas BaaS.

## TED

- [x] Receber transferências TED na conta BaaS.
- [x] Realizar transferência via TED.
- [x] Consultar status de transferência TED.
- [x] Implementar modelos de webhooks TED.

## Débito Veicular

- [x] Consultar débitos veiculares.
- [x] Efetivar pagamento de débitos veiculares.
- [x] Implementar débitos dependentes, distintos e obrigatórios.
- [x] Criar massa de testes de débito veicular.
- [x] Documentar FAQs.

## Recargas

- [x] Consultar operadoras e valores.
- [x] Realizar recarga.

## SLC

- [x] Implementar recebimento de liquidações do arranjo de cartões via SLC.

## Informe de Rendimentos

- [x] Informe de rendimentos PF.
- [x] Informe de rendimentos PJ.

## Webhooks BaaS

- [x] Criar base de recebimento de webhooks.
- [x] Persistir eventos de webhook.
- [x] Deduplicar eventos de webhook.
- [x] Reprocessar webhook por endpoint administrativo.
- [x] Cadastrar e gerenciar webhooks na Celcoin.
- [x] Reenvio de webhook na Celcoin.
- [x] Templates de webhooks BaaS.
- [x] Bloqueio/desbloqueio de saldo por infração.

## Tabela de Erros BaaS

- [x] Mapear erros por módulo.
- [x] Criar exceções específicas por código remoto.
- [x] Criar testes de mapeamento de erro.

## MED 2.0

- [x] Criar recuperação de valores.
- [x] Consultar recuperação de valores.
- [x] Cancelar recuperação de valores.
- [x] Receber recuperação de valores.
- [x] Bloquear valores.
- [x] Desbloquear valores.
- [x] Devolver valores.
- [x] Implementar webhooks MED 2.0.

## Core Banking - Bloqueio e Desbloqueio de Saldo

- [x] Bloquear saldo.
- [x] Desbloquear saldo.
- [x] Criar cenários práticos.

## cel_banking - cards

- [x] Documentar sobre cel_card.
- [x] Criar conta cartão.
- [x] Consultar dados de conta cartão.
- [x] Consultar limites da conta cartão.
- [x] Atualizar dados cadastrais.
- [x] Atualizar telefone.
- [x] Cancelar conta e cartão.
- [x] Implementar endereços.
- [x] Emitir cartão.
- [x] Emitir segunda via de cartão.
- [x] Consultar rastreio.
- [x] Simular rastreio.
- [x] Ativar cartão.
- [x] Alterar status.
- [x] Visualizar dados do cartão.
- [x] Listar cartões.
- [x] Alterar senha Pin Online e Pin Offline.
- [x] Implementar simulador de transações.
- [x] Cadastrar e gerenciar webhooks de cartão.
- [x] Template de webhooks de cartão.
- [x] Reenvio de webhook pendente.
- [x] Consultar dados de fatura pós-paga.

## cel_credit

- [x] Implementar autenticação de crédito.
- [x] Implementar variáveis personalizadas.
- [x] Campos de solicitação.
- [x] Simulações.
- [x] Status de solicitação.
- [x] Tabela de erros de geração de boleto.
- [x] Assinatura CCB por modalidade.
- [x] Assinatura via cláusula mandato timestamp.
- [x] Assinatura via envio de PDF.
- [x] Consulta de assinaturas da CCB.
- [x] Webhooks de crédito.

### Consignado Crédito Trabalhador

- [x] Solicitação de proposta e distribuição aos originadores.
- [x] Oferta, leilão, devolutiva e emissão.
- [x] Solicitação e consentimento do tomador.
- [x] Consulta de margem e simulação de crédito.
- [x] Consulta de saldo FGTS e multa rescisória FGTS.
- [x] Cadastro do tomador, emissão e cancelamento.
- [x] Guia rápido consignado.
- [x] Falhas de averbação Dataprev.
- [x] Status da operação.
- [x] Consultas, escriturações e repasses.
- [x] Consulta status da garantia.

### Empréstimo Consignado Servidores do Exército

- [x] Autenticação.
- [x] Consulta de margem.
- [x] Simulação da CCB.
- [x] Criação do tomador.
- [x] Compra com troco.
- [x] Status da operação.

### Conta Escrow

- [x] Onboarding conta escrow.
- [x] Fluxo essencial.
- [x] Depósitos.
- [x] Destinatários e cobranças.
- [x] Permissões.

### Portabilidade de Crédito

- [x] Portabilidade no ambiente do originador.
- [x] Simulação de portabilidade de crédito do trabalhador.
- [x] Autorização de consulta e consulta de vínculo empregatício.
- [x] Cadastro do tomador e emissão da CCB de portabilidade.
- [x] Solicitação do bundle/proposta de portabilidade.
- [x] Averbação por portabilidade e envio de contrato: disparados pelo fluxo Celcoin/Dataprev após a solicitação do bundle.

## cel_banking - embedded solutions

- [x] Conta Celcoin.
- [x] DDA.
- [x] Tabela de erros DDA.
- [x] FAQs DDA.
- [x] Débitos veiculares embedded.
- [x] Enriquecimento de placas.
- [x] Webhook com autenticação JWT.
- [x] Idempotência de débito veicular.
- [x] NFS-e.
- [x] Tabela de erros NFS-e.
- [x] Boletos conta única direto.
- [x] Gestão de beneficiários.
- [x] Gestão de carteiras.
- [x] Gestão de emissão de boletos.
- [x] Gestão de baixa de boletos.
- [x] Pagamento de contas embedded.
- [x] Devolução de pagamento de conta.
- [x] Massa de dados para testes.
- [x] Lista de convênios.
- [x] Pix avulso embedded.
- [x] Recargas nacionais.
- [x] Saques e depósitos físicos: parceiros, pontos de atendimento, depósito, saque, token e cancelamento implementados.
- [x] TED embedded.
- [x] Conciliação: operações e extrato consolidado implementados no módulo `reconciliation`.
- [x] SLC Celcoin como banco liquidante.

## Pix Indireto - Produto Dedicado

- [x] Listar todas as chaves Pix de cliente.
- [x] Portabilidade e reivindicação.
- [x] Infrações.
- [x] MED.
- [x] Marcação de fraude.
- [x] Criação de recuperação de valores.
- [x] Consulta de recuperação de valores.
- [x] Cancelamento de recuperação de valores.
- [x] Solicitação de devolução em recuperação de valores.
- [x] Consulta de grafo de recuperação de valores.
- [x] Atualização de recuperação de valores.
- [x] Status da recuperação de valores.
- [x] Fechar recuperação de valores.
- [x] Fechar devolução em recuperação de valores.
- [x] Autorização do Cash-in (modelo de payload e parser de resposta).
- [x] QR Code Pix Indireto.
- [x] Pagamentos Pix cash-out para indiretos.
- [x] Recebimentos Pix cash-in.
- [x] Devolução de recebimentos Pix.
- [x] Reporte Bacen de transações fora do SPI.
- [x] Pix Saque/Troco (via `transactionType` no pagamento).
- [x] Tabela de erros para participantes indiretos.
- [x] Webhooks do participante indireto (modelo e parser).

## CEL_BRICKS WEBHOOKS

- [x] Webhook Manager.
- [x] Cadastrar e gerenciar webhooks.
- [x] Reenvio de webhook.

## cel_open

### Compartilhamento de Dados

- [x] Consentimento.
- [x] Direcionamento.
- [x] Autenticação.
- [x] Confirmação.
- [x] Redirecionamento.
- [x] Documentação técnica transmissora.
- [x] Documentação técnica receptora.

### Jornada Com Redirecionamento

- [x] Criar iniciação de pagamento v4.
- [x] Callback da iniciação de pagamento.
- [x] PIX v4.
- [x] Criar jornada de pagamento v4.
- [x] Listar jornadas de pagamento v4.
- [x] Buscar jornada de pagamento v4.

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
- [x] Listar jornadas de vínculo via JSR/enrollments.
- [x] Buscar jornada de vínculo via JSR/enrollments.
- [x] Criar jornada de pagamento v4.
- [x] Listar jornadas de pagamento v4 via JSR.
- [x] Buscar jornada de pagamento v4 via JSR.

### Brick Bank e Brick Insurance

- [x] Brick Bank receptora de dados via `openFinance()`.
- [~] Brick Bank transmissora de dados: APIs de exposição como transmissora dependem do contrato e da infraestrutura da instituição.
- [~] Brick Bank detentora de contas: endpoints de autenticação/confirmação da detentora não estão publicados no contrato SDK.
- [~] Brick Insurance receptora de dados: produto e contrato específico não publicados para o SDK.
- [~] Brick Insurance transmissora de dados: produto e contrato específico não publicados para o SDK.

### Open Plus

- [~] Portal: cadastro e acesso: jornada realizada no portal web Open Plus.
- [~] Hall Open Plus: interface administrativa do portal.
- [~] Gestão de aplicações: recurso do portal, sem API pública para o SDK.
- [~] Catálogo de produtos: recurso do portal, sem API pública para o SDK.
- [~] Gestão de membros da equipe: recurso do portal, sem API pública para o SDK.
- [~] Menu de usuário: recurso do portal, sem API pública para o SDK.
- [~] Painel de gestão da aplicação: recurso do portal.
- [~] Painel de gestão do produto: recurso do portal.
- [x] Documentação de produto.
- [~] Credenciais e usuário de teste: provisionados pelo portal/contrato.
- [~] Subir em produção: processo de publicação e homologação do portal.
- [~] Portal configurações: painel administrativo do produto.

### Plataforma de Iniciação ITP

- [~] Dashboard Open Keys: recurso do portal Open Plus, sem endpoint público no contrato SDK.
- [~] Lista de transações: consulta disponível no painel; APIs dependem do produto contratado.
- [x] Lista de vínculos de conta via JSR/enrollments.
- [~] Relatórios: recurso do portal Open Keys.
- [~] Configurações da jornada de pagamentos: recurso do portal Open Keys.
- [~] Demo das jornadas de pagamento: recurso do portal Open Keys.
- [~] Demo da área de gestão: recurso do portal Open Keys.
- [x] Iniciação de pagamento instantâneo.
- [x] Gestão de pagamentos instantâneos.
- [x] Iniciação de pagamento agendado.
- [x] Gestão de pagamentos agendados.
- [x] Iniciação de pagamento sem redirecionamento.
- [x] Gestão de contas salvas.
- [x] Iniciação de pagamento automático.
- [x] Gestão de pagamentos automáticos.
- [x] FAQ Open Keys.
- [~] Jornada Pay by Link: depende de contratação/jornada white-label específica.
- [x] Pix por aproximação via JSR, FIDO2 e NFC no aplicativo cliente.
- [x] Máquina de estados.
- [x] Códigos de resposta HTTP e tratamento comum do SDK.
- [x] Possíveis erros de pagamentos.
- [x] Diagrama de sequência com redirecionamento.
- [x] Diagrama de sequência sem redirecionamento.
- [x] Webhook.
- [x] APIs públicas do SDK para ITP, JSR e Pix Automático.
- [x] Criar consentimento Sweeping Accounts.
- [x] Callback de consentimento Sweeping Accounts.
- [x] Cancelamento de consentimento Sweeping Accounts.
- [x] Listar consentimentos.
- [x] Buscar consentimento.
- [x] Criação de sessão de jornada.
- [x] Listar sessões de jornada.
- [x] Buscar sessão de jornada.
- [x] Pix inteligente.

## Pix - Avulso

- [x] Documentar sobre Pix avulso.
- [x] Cobrança imediata com QR Code dinâmico immediate.
- [x] Cobrança com vencimento com QR Code dinâmico duedate.
- [x] Cobrança estática.
- [x] Consulta de recebimentos Pix.
- [x] Devolução de recebimentos Pix.
- [x] DICT e balde de fichas.
- [x] Transferência Pix por dados bancários.
- [x] Transferência Pix por chave.
- [x] Pagamento de QR Code estático.
- [x] Pagamento de QR Code dinâmico.
- [x] Pix entre contas.
- [x] Consultar transferências, pagamentos e devoluções Pix-out.
- [x] Pagamento de QR Code estático ou dinâmico.
- [x] Pix Automático pagador.
- [x] Pix Automático recebedor.
- [x] Tabela de erros Pix.
- [x] FAQs Pix.
- [x] Modelos de webhooks Pix.

## Conciliação

- [x] API para conciliação.
- [x] Documentar API de conciliação.
- [x] API para extrato consolidado.
- [x] Documentar API de extrato consolidado.
- [x] Tabela de erros.

## Suporte

- [~] Central de ajuda: canal e conteúdo dependem do suporte contratado/Celcoin.
- [~] SLA de atendimento: definido contratualmente; não há endpoint público no contrato REST.

## Painel do Cliente

- [~] Listagem de tickets: disponível no Painel do Cliente, sem API pública mapeada.

## Anti-fraudes

- [x] Follow the Money: consulta pontual de risco implementada; configuração de regras permanece dependente do Painel/contrato.
- [x] Regras antifraude por transação: normalização local das decisões recebidas, preservando o payload original.
- [x] Webhooks antifraude: receptor idempotente, parser e suporte ao ciclo de retry.
- [x] Auditoria e retenção de eventos antifraude: persistência local e purge explícito por cutoff.

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
