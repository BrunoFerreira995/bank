# Cobertura E2E do frontend

Este documento é a fonte de verdade dos testes end-to-end do frontend. O
status `[x]` significa que o cenário foi executado e passou; `[ ]` significa
que ainda não existe execução E2E válida. Testes unitários, mocks de contrato e
build não contam como E2E.

## Execução atual

| Plataforma | Ambiente | Runner | Resultado |
|---|---|---|---|
| Web Chromium | BFF mockado no Playwright | Playwright | 33 cenários passando |
| Android | staging/sandbox | Detox/Appium | Pendente |
| iOS | staging/sandbox | Detox | Suíte implementada; execução depende de simulador e massa |

Comando web:

```bash
cd frontend
npm run test:e2e:web
```

Comandos iOS:

```bash
cd frontend
cp .env.e2e-ios.example .env.e2e-ios
npx pod-install ios
npm run build:e2e:ios
npm run test:e2e:ios
```

Os testes nativos iOS estão em [`frontend/e2e/ios`](../frontend/e2e/ios) e
cobrem inicialização sem sessão, login (incluindo MFA opcional), logout,
abertura de conta e deep link. O resultado só deve ser marcado como executado
após rodar contra staging e sandbox com massa isolada e evidência publicada.

### Status da suíte iOS

| Suíte | Arquivo | Cenários implementados | Status de execução |
|---|---|---:|---|
| Sessão | [`session.e2e.ts`](../frontend/e2e/ios/session.e2e.ts) | 2 | Implementada; staging/sandbox pendentes |
| Navegação pública | [`navigation.e2e.ts`](../frontend/e2e/ios/navigation.e2e.ts) | 2 | Implementada; staging/sandbox pendentes |
| Identidade e KYC | [`kyc.e2e.ts`](../frontend/e2e/ios/kyc.e2e.ts) | 4 | Implementada; staging/sandbox pendentes |
| Conta e movimentações | [`account.e2e.ts`](../frontend/e2e/ios/account.e2e.ts) | 4 | Implementada; staging/sandbox pendentes |
| Pix | [`pix.e2e.ts`](../frontend/e2e/ios/pix.e2e.ts) | 8 | Implementada; staging/sandbox pendentes |
| Boletos, recargas e débitos | [`payments.e2e.ts`](../frontend/e2e/ios/payments.e2e.ts) | 6 | Implementada; staging/sandbox pendentes |

A coluna `Android/iOS` das matrizes abaixo permanece `[ ]` até que o cenário
seja executado nos dispositivos e ambientes exigidos. Implementação da suíte,
build local ou validação de configuração não substituem evidência de execução.

## Fundação e sessão

### Implementação disponível

O frontend já possui a fundação de sessão necessária para os fluxos abaixo:

- configuração por ambiente e feature flags;
- persistência segura da sessão via Keychain/Keystore (com adaptador de memória
  somente para a web);
- restauração da sessão na inicialização, validação de expiração e limpeza de
  credenciais inválidas;
- logout com tentativa de revogação no BFF e limpeza local garantida;
- remoção automática da sessão quando uma operação autenticada recebe `401`;
- login, MFA, recuperação de acesso e troca de senha no contrato `/mobile/v1`.

Essa seção descreve o estado da implementação. Os marcadores da matriz abaixo
continuam reservados para execuções E2E válidas no ambiente e nas plataformas
correspondentes.

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Inicialização do app | [x] | [ ] | Renderiza sem erro e carrega configuração |
| Feature flags | [ ] | [ ] | Produto habilitado/desabilitado |
| Login aceito | [x] | [ ] | Credenciais válidas criam sessão |
| Login recusado | [x] | [ ] | Erro seguro sem token na tela |
| MFA | [x] | [ ] | Desafio aceito, recusado e expirado |
| Recuperação de acesso | [x] | [ ] | Solicitação e mensagem neutra |
| Troca de senha | [x] | [ ] | Sucesso, senha inválida e sessão expirada |
| Logout | [x] | [ ] | Limpa sessão e retorna ao login |
| Sessão expirada | [x] | [ ] | Redireciona e remove dados sensíveis |
| Deep link | [x] | [ ] | Abre a rota correta |

## Identidade e onboarding KYC

#### Cobertura iOS implementada

A suíte [`kyc.e2e.ts`](../frontend/e2e/ios/kyc.e2e.ts) cobre quatro cenários
executáveis no iOS:

- recusa de CPF inválido antes da chamada ao BFF;
- início de cadastro PF com endereço e consentimentos;
- bloqueio de cadastro PJ com representante inválido;
- início de cadastro PJ com representante e consentimentos.

Os dados válidos de PF/PJ e representante são fornecidos pelo secret store da
CI por meio das variáveis `E2E_KYC_*`. Os cenários só devem ser promovidos para
`[x]` após execução em staging e sandbox com massa isolada e evidência
arquivada.

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Cadastro PF | [x] | [ ] | CPF, contato e endereço válidos |
| Cadastro PJ | [x] | [ ] | CNPJ, representantes e sócios |
| Consentimentos | [x] | [ ] | Termos versionados e recusa |
| Upload de documentos | [ ] | [ ] | Progresso, retry e retomada |
| Permissão de captura | [ ] | [ ] | Permissão negada bloqueia captura |
| Status KYC pendente | [ ] | [ ] | Exibe pendência e próxima ação |
| Status KYC aprovado | [ ] | [ ] | Libera acesso ao produto |
| Status KYC recusado | [ ] | [ ] | Exibe motivo seguro e correção |
| Biometria/liveness | [ ] | [ ] | Sucesso, recusa e fallback |

## Conta, saldo e movimentações

#### Cobertura iOS implementada

A suíte [`account.e2e.ts`](../frontend/e2e/ios/account.e2e.ts) cobre dashboard,
saldo disponível e bloqueado, movimentações do dia, troca de conta, consulta e
filtro do extrato, refresh da conta e validação de troca de senha no perfil.
Os cenários dependem de `E2E_USER_IDENTIFIER`, `E2E_USER_PASSWORD` e, quando
aplicável, `E2E_MFA_CODE`.

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Dashboard | [x] | [ ] | Conta, saldo e status |
| Saldo bloqueado | [x] | [ ] | Exibe saldo disponível e bloqueado |
| Troca de conta | [x] | [ ] | Alterna conta ativa |
| Extrato | [x] | [ ] | Paginação, filtro e vazio |
| Movimentações do dia | [x] | [ ] | Refresh e erro |
| Perfil | [x] | [ ] | Consulta e edição cadastral |
| Encerramento | [ ] | [ ] | Confirmação, motivo e status |
| Bloqueio judicial | [x] | [ ] | Consulta sem ação decisória no app |
| Informe de rendimentos | [ ] | [ ] | Download HTTPS e erro |
| Transferência/TED | [x] | [ ] | Confirmação, idempotência e comprovante |

## Pix

#### Cobertura iOS implementada

A suíte [`pix.e2e.ts`](../frontend/e2e/ios/pix.e2e.ts) cobre validações de
pagamento, Pix por chave, Pix por dados bancários, QR inválido, cobrança
imediata, abertura da gestão de chaves e criação de chave Pix. Os fluxos
financeiros usam massa isolada fornecida por `E2E_PIX_*` no secret store da CI.

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Pix por chave | [ ] | [ ] | Favorecido, confirmação e pagamento |
| Pix por dados bancários | [x] | [ ] | Dados válidos e inválidos |
| QR estático | [x] | [ ] | Leitura, validação e pagamento |
| QR dinâmico | [x] | [ ] | Vencimento, valor e pagamento |
| Cobrança immediate | [x] | [ ] | Criação e exibição |
| Cobrança duedate | [ ] | [ ] | Criação, vencimento e baixa |
| Cobrança estática | [ ] | [ ] | Criação e pagamento |
| Devolução | [ ] | [ ] | Solicitação, status e erro |
| Bloqueio cautelar | [ ] | [ ] | Status refletido pelo BFF |
| Chaves Pix | [ ] | [ ] | Criar, listar, alterar e excluir |
| Portabilidade/reivindicação | [ ] | [ ] | Timeline e estados |
| Pix Automático | [ ] | [ ] | Pagador, recebedor e cancelamento |
| Pix Indireto/Avulso/Inteligente | [ ] | [ ] | Feature flag e operação |
| Pix Saque/Troco | [ ] | [ ] | Produto habilitado e comprovante |
| Comprovante Pix | [ ] | [ ] | Download/compartilhamento seguro |

## Boletos, recargas e débitos

#### Cobertura iOS implementada

A suíte [`payments.e2e.ts`](../frontend/e2e/ios/payments.e2e.ts) cobre validação
de boleto, consulta e pagamento, validação e execução de recarga, além de
consulta e pagamento de débitos veiculares abertos. Os fluxos financeiros usam
massa isolada fornecida por `E2E_BILL_CODE`, `E2E_TOPUP_*` e
`E2E_VEHICLE_*` no secret store da CI.

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Consulta de boleto | [x] | [ ] | Linha digitável/código inválido e válido |
| Pagamento de boleto | [x] | [ ] | Autorização, pagamento e comprovante |
| Boleto vencido | [ ] | [ ] | Tratamento de vencimento |
| Cancelamento de boleto | [ ] | [ ] | Confirmação e status |
| Emissão de boleto | [ ] | [ ] | Produto habilitado |
| Recarga | [x] | [ ] | Operadora, produto, valor e telefone |
| Reprocessamento de recarga | [ ] | [ ] | Retry com mesma idempotência |
| Débito veicular | [x] | [ ] | Consulta e seleção |
| Pagamento veicular | [x] | [ ] | Valor, pagamento e comprovante |

## Cartões, crédito e Escrow

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Solicitação de cartão | [ ] | [ ] | Virtual/físico e elegibilidade |
| Ativação | [x] | [ ] | Código válido e inválido |
| Bloqueio/desbloqueio | [x] | [ ] | Confirmação e estado |
| Limite/fatura/transações | [ ] | [ ] | Consulta, vazio e erro |
| Proteção PAN/CVV | [ ] | [ ] | Nunca exibe dados completos |
| Simulação de crédito | [x] | [ ] | Valor, parcelas e erro |
| Proposta de crédito | [ ] | [ ] | Documentos e análise |
| Consignados | [ ] | [ ] | Elegível, inelegível e pendente |
| Portabilidade de crédito | [ ] | [ ] | Solicitação e timeline |
| Conta Escrow | [x] | [ ] | Saldo, partes, eventos e estados |

## Open Finance, ITP e jornadas

### Implementação disponível

O frontend web já possui suporte para:

- seleção de instituição e criação, consulta e revogação de consentimentos;
- redirecionamento Open Finance validado exclusivamente para URLs HTTPS;
- pagamentos imediato, agendado e automático;
- consulta de jornadas/links;
- sweeping de contas;
- sessões Brick Bank e Brick Insurance.

Os fluxos adicionais foram validados pela regressão E2E web. A matriz só marca
`[x]` quando há um cenário E2E específico para o comportamento descrito. A
suíte web atual contém 33 cenários passando; linhas sem tela ou contrato de
UX implementado permanecem pendentes.

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Seleção de instituição | [x] | [ ] | Disponível/indisponível |
| Consentimento | [ ] | [ ] | Autorizar, recusar, duplicar e revogar |
| Redirecionamento | [ ] | [ ] | Callback, timeout e expiração |
| Jornada sem redirect | [ ] | [ ] | Passkey/FIDO2 e fallback |
| Pagamento imediato | [x] | [ ] | Confirmação e status |
| Pagamento agendado | [x] | [ ] | Agendamento e cancelamento |
| Pagamento automático | [x] | [ ] | Autorização e revogação |
| Sweeping Accounts | [x] | [ ] | Transferência e idempotência |
| Brick Bank/Insurance | [x] | [ ] | Feature flag e indisponibilidade |

## Notificações, suporte e operação

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Registro de push token | [ ] | [ ] | Registro e rotação |
| Push seguro | [ ] | [ ] | Payload não altera saldo |
| Central de notificações | [x] | [ ] | Listar e marcar lida |
| FAQ | [x] | [ ] | Listagem e vazio |
| Tickets | [x] | [ ] | Abrir, listar e acompanhar |
| Status de serviços | [x] | [ ] | Operacional, manutenção e outage |
| Erros operacionais | [ ] | [ ] | Código Celcoin e mensagem segura |

## Critério de fechamento

Uma feature só pode ser marcada `[x]` quando houver teste passando na matriz
correspondente, em todos os ambientes e plataformas exigidos pelo contrato,
com evidência arquivada. O resultado web atual é parcial: 33 cenários passam;
a suíte iOS está implementada, mas aguarda execução em staging e sandbox; as
demais linhas aguardam implementação ou execução E2E.
