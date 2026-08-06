# Cobertura E2E do frontend

Este documento é a fonte de verdade dos testes end-to-end do frontend. O
status `[x]` significa que o cenário foi executado e passou; `[ ]` significa
que ainda não existe execução E2E válida. Testes unitários, mocks de contrato e
build não contam como E2E.

## Execução atual

| Plataforma | Ambiente | Runner | Resultado |
|---|---|---|---|
| Web Chromium | BFF mockado no Playwright | Playwright | 2 cenários passando |
| Android | staging/sandbox | Detox/Appium | Pendente |
| iOS | staging/sandbox | Detox/Appium | Pendente |

Comando web:

```bash
cd frontend
npm run test:e2e:web
```

## Fundação e sessão

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Inicialização do app | [ ] | [ ] | Renderiza sem erro e carrega configuração |
| Feature flags | [ ] | [ ] | Produto habilitado/desabilitado |
| Login aceito | [x] | [ ] | Credenciais válidas criam sessão |
| Login recusado | [x] | [ ] | Erro seguro sem token na tela |
| MFA | [ ] | [ ] | Desafio aceito, recusado e expirado |
| Recuperação de acesso | [ ] | [ ] | Solicitação e mensagem neutra |
| Troca de senha | [ ] | [ ] | Sucesso, senha inválida e sessão expirada |
| Logout | [ ] | [ ] | Limpa sessão e retorna ao login |
| Sessão expirada | [ ] | [ ] | Redireciona e remove dados sensíveis |
| Deep link | [ ] | [ ] | Abre a rota correta |

## Identidade e onboarding KYC

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Cadastro PF | [ ] | [ ] | CPF, contato e endereço válidos |
| Cadastro PJ | [ ] | [ ] | CNPJ, representantes e sócios |
| Consentimentos | [ ] | [ ] | Termos versionados e recusa |
| Upload de documentos | [ ] | [ ] | Progresso, retry e retomada |
| Permissão de captura | [ ] | [ ] | Permissão negada bloqueia captura |
| Status KYC pendente | [ ] | [ ] | Exibe pendência e próxima ação |
| Status KYC aprovado | [ ] | [ ] | Libera acesso ao produto |
| Status KYC recusado | [ ] | [ ] | Exibe motivo seguro e correção |
| Biometria/liveness | [ ] | [ ] | Sucesso, recusa e fallback |

## Conta, saldo e movimentações

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Dashboard | [x] | [ ] | Conta, saldo e status |
| Saldo bloqueado | [ ] | [ ] | Exibe saldo disponível e bloqueado |
| Troca de conta | [ ] | [ ] | Alterna conta ativa |
| Extrato | [ ] | [ ] | Paginação, filtro e vazio |
| Movimentações do dia | [ ] | [ ] | Refresh e erro |
| Perfil | [ ] | [ ] | Consulta e edição cadastral |
| Encerramento | [ ] | [ ] | Confirmação, motivo e status |
| Bloqueio judicial | [ ] | [ ] | Consulta sem ação decisória no app |
| Informe de rendimentos | [ ] | [ ] | Download HTTPS e erro |
| Transferência/TED | [ ] | [ ] | Confirmação, idempotência e comprovante |

## Pix

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Pix por chave | [ ] | [ ] | Favorecido, confirmação e pagamento |
| Pix por dados bancários | [ ] | [ ] | Dados válidos e inválidos |
| QR estático | [ ] | [ ] | Leitura, validação e pagamento |
| QR dinâmico | [ ] | [ ] | Vencimento, valor e pagamento |
| Cobrança immediate | [ ] | [ ] | Criação e exibição |
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

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Consulta de boleto | [ ] | [ ] | Linha digitável/código inválido e válido |
| Pagamento de boleto | [ ] | [ ] | Autorização, pagamento e comprovante |
| Boleto vencido | [ ] | [ ] | Tratamento de vencimento |
| Cancelamento de boleto | [ ] | [ ] | Confirmação e status |
| Emissão de boleto | [ ] | [ ] | Produto habilitado |
| Recarga | [ ] | [ ] | Operadora, produto, valor e telefone |
| Reprocessamento de recarga | [ ] | [ ] | Retry com mesma idempotência |
| Débito veicular | [ ] | [ ] | Consulta e seleção |
| Pagamento veicular | [ ] | [ ] | Valor, pagamento e comprovante |

## Cartões, crédito e Escrow

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Solicitação de cartão | [ ] | [ ] | Virtual/físico e elegibilidade |
| Ativação | [ ] | [ ] | Código válido e inválido |
| Bloqueio/desbloqueio | [ ] | [ ] | Confirmação e estado |
| Limite/fatura/transações | [ ] | [ ] | Consulta, vazio e erro |
| Proteção PAN/CVV | [ ] | [ ] | Nunca exibe dados completos |
| Simulação de crédito | [ ] | [ ] | Valor, parcelas e erro |
| Proposta de crédito | [ ] | [ ] | Documentos e análise |
| Consignados | [ ] | [ ] | Elegível, inelegível e pendente |
| Portabilidade de crédito | [ ] | [ ] | Solicitação e timeline |
| Conta Escrow | [ ] | [ ] | Saldo, partes, eventos e estados |

## Open Finance, ITP e jornadas

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Seleção de instituição | [ ] | [ ] | Disponível/indisponível |
| Consentimento | [ ] | [ ] | Autorizar, recusar, duplicar e revogar |
| Redirecionamento | [ ] | [ ] | Callback, timeout e expiração |
| Jornada sem redirect | [ ] | [ ] | Passkey/FIDO2 e fallback |
| Pagamento imediato | [ ] | [ ] | Confirmação e status |
| Pagamento agendado | [ ] | [ ] | Agendamento e cancelamento |
| Pagamento automático | [ ] | [ ] | Autorização e revogação |
| Sweeping Accounts | [ ] | [ ] | Transferência e idempotência |
| Brick Bank/Insurance | [ ] | [ ] | Feature flag e indisponibilidade |

## Notificações, suporte e operação

| Feature | E2E web | Android/iOS | Cenário |
|---|---:|---:|---|
| Registro de push token | [ ] | [ ] | Registro e rotação |
| Push seguro | [ ] | [ ] | Payload não altera saldo |
| Central de notificações | [ ] | [ ] | Listar e marcar lida |
| FAQ | [ ] | [ ] | Listagem e vazio |
| Tickets | [ ] | [ ] | Abrir, listar e acompanhar |
| Status de serviços | [ ] | [ ] | Operacional, manutenção e outage |
| Erros operacionais | [ ] | [ ] | Código Celcoin e mensagem segura |

## Critério de fechamento

Uma feature só pode ser marcada `[x]` quando houver teste passando na matriz
correspondente, em todos os ambientes e plataformas exigidos pelo contrato,
com evidência arquivada. O resultado atual é deliberadamente parcial: 2
cenários web passam; as demais linhas aguardam implementação/execução E2E.
