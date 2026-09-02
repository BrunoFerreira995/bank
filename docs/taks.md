# Codex Project Roadmap

> Fonte de verdade para orientar as prÃ³ximas implementaÃ§Ãµes do projeto `BrunoFerreira995/bank`.

## 1. Objetivo do projeto

Entregar uma plataforma bancÃ¡ria open source composta por:

- SDK Spring Boot modular para integraÃ§Ã£o com produtos Celcoin;
- BFF institucional seguro, responsÃ¡vel pelos contratos `/mobile/v1/*`;
- frontend web responsivo baseado em React Native Web e Vite;
- aplicativos iOS e Android compartilhando a base React Native;
- persistÃªncia, auditoria, idempotÃªncia, webhooks, observabilidade e testes;
- homologaÃ§Ã£o em sandbox/staging antes de qualquer operaÃ§Ã£o com dinheiro real.

## 2. Estado atual

### Backend/SDK

- Java 21 e Spring Boot 3.5;
- mÃ³dulos de contas, Pix, Pix AutomÃ¡tico, Pix Indireto, boletos, recargas,
  dÃ©bitos veiculares, cartÃµes, crÃ©dito, Escrow, Open Finance, ITP, CNAB,
  adquirÃªncia, antifraude, conciliaÃ§Ã£o, sweeping, SLC e webhooks;
- PostgreSQL, JPA e Flyway;
- OAuth, cache de token, rate limit, mTLS configurÃ¡vel e idempotÃªncia;
- testes com JUnit, WireMock e Testcontainers;
- BFF institucional v1 iniciado, com contratos de saldo e decodificaÃ§Ã£o Pix;
- BFF ainda nÃ£o integrado ao frontend e sem sessÃ£o/autorizaÃ§Ã£o de usuÃ¡rio.

### Frontend web

- React 19, React Native Web, Vite, TypeScript, React Query, Zustand e Paper;
- login, MFA, onboarding, dashboard, extrato, Pix, pagamentos, produtos,
  Open Finance, serviÃ§os, suporte e perfil;
- layout responsivo para mobile, tablet, notebook e desktop;
- Playwright com BFF mockado;
- correÃ§Ãµes visuais ainda pendentes em alguns viewports.

### Mobile nativo

- base React Native existente;
- suÃ­te Detox iOS parcialmente implementada;
- execuÃ§Ã£o real de iOS e Android, distribuiÃ§Ã£o, pentest e publicaÃ§Ã£o pendentes;
- iOS e Android nÃ£o devem ser descritos como concluÃ­dos.

## 3. Regras obrigatÃ³rias para o Codex

1. Antes de implementar, inspecionar o cÃ³digo, testes, documentaÃ§Ã£o e contratos
   relacionados Ã  tarefa.
2. Preservar as APIs pÃºblicas existentes do SDK, salvo quando uma mudanÃ§a
   incompatÃ­vel for explicitamente aprovada.
3. O frontend nunca deve chamar a Celcoin diretamente.
4. Segredos, mTLS, SFTP, antifraude, limites e decisÃµes financeiras permanecem
   no backend/BFF.
5. NÃ£o inventar endpoints Celcoin ou afirmar suporte a produtos sem contrato
   confirmado.
6. Toda mutaÃ§Ã£o financeira deve possuir idempotÃªncia, confirmaÃ§Ã£o, resultado
   seguro e rastreabilidade.
7. Nunca registrar token, senha, CPF/CNPJ completo, PAN, CVV ou segredo.
8. NÃ£o marcar uma tarefa como concluÃ­da apenas porque compila; executar os
   testes e critÃ©rios de aceite relevantes.
9. NÃ£o marcar E2E nativo ou sandbox como concluÃ­do sem execuÃ§Ã£o e evidÃªncia.
10. NÃ£o fazer commit, push, merge, deploy ou publicaÃ§Ã£o sem autorizaÃ§Ã£o.
11. Manter alteraÃ§Ãµes pequenas, modulares e cobertas por testes.
12. Ao finalizar uma tarefa, documentar arquivos alterados, testes executados,
    resultados, riscos restantes e prÃ³ximos passos.

## 4. Prioridades

| Prioridade | Significado |
|---|---|
| P0 | Bloqueia seguranÃ§a, integraÃ§Ã£o ou operaÃ§Ãµes financeiras |
| P1 | NecessÃ¡rio para uma versÃ£o web completa em staging |
| P2 | NecessÃ¡rio para qualidade, operaÃ§Ã£o e homologaÃ§Ã£o |
| P3 | EvoluÃ§Ã£o de produto ou aplicativo nativo |

## 5. P0 â€” BFF institucional

### 5.1 FundaÃ§Ã£o do BFF

- [x] Criar mÃ³dulo/aplicaÃ§Ã£o responsÃ¡vel pelos contratos `/mobile/v1/*`.
- [x] Definir uma arquitetura por domÃ­nio, sem colocar todos os endpoints em um
      Ãºnico controller ou service.
- [x] Reutilizar `CelcoinClient` e interfaces `*Operations` do SDK.
- [x] Criar contratos versionados de request, response e erro.
- [x] Implementar correlaÃ§Ã£o de requisiÃ§Ãµes e auditoria.
- [x] Definir configuraÃ§Ã£o separada para local, test, staging e production.
- [x] Adicionar documentaÃ§Ã£o OpenAPI dos contratos do BFF.

Implementado em `src/main/java/com/brunopedraca/celcoin/bff`, com contratos
iniciais para saldo de conta e decodificaÃ§Ã£o Pix. Consulte
`docs/mobile-bff.md` para endpoints, correlaÃ§Ã£o, auditoria e OpenAPI.

### 5.2 SessÃ£o e identidade

- [x] Implementar `POST /mobile/v1/session`.
- [x] Implementar desafio e validaÃ§Ã£o de MFA.
- [x] Implementar recuperaÃ§Ã£o de acesso com resposta neutra.
- [x] Implementar renovaÃ§Ã£o e expiraÃ§Ã£o da sessÃ£o.
- [x] Implementar `DELETE /mobile/v1/session` com revogaÃ§Ã£o.
- [x] Relacionar usuÃ¡rio autenticado Ã s contas autorizadas.
- [x] Impedir enumeraÃ§Ã£o de usuÃ¡rios e exposiÃ§Ã£o de detalhes de autenticaÃ§Ã£o.

SessÃµes usam tokens opacos hasheados, rotaÃ§Ã£o de refresh e revogaÃ§Ã£o. MFA
usa TOTP com desafio curto e limitado; recuperaÃ§Ã£o responde `202` de forma
neutra. A entrega efetiva da recuperaÃ§Ã£o e o provisionamento administrativo de
usuÃ¡rios/MFA continuam dependentes de um provedor de notificaÃ§Ãµes e back-office
aprovados.

### 5.3 AutorizaÃ§Ã£o

- [x] Validar ownership da conta em toda consulta e mutaÃ§Ã£o.
- [x] Implementar RBAC/ABAC para cliente, suporte, operaÃ§Ã£o e administrador.
- [x] Impedir troca de `accountId` para acessar conta de outro usuÃ¡rio.
- [x] Exigir step-up authentication para operaÃ§Ãµes de risco quando aplicÃ¡vel.
- [x] Criar testes negativos de acesso entre usuÃ¡rios e contas.

O BFF usa roles e grants explÃ­citos por conta, sem permissÃ£o privilegiada
global. As operaÃ§Ãµes de risco devem chamar `requireRisk`, que exige step-up
TOTP fresco; a polÃ­tica possui testes negativos para contas de terceiros,
escrita por suporte e administrador sem grant.

### 5.4 Contratos bancÃ¡rios do BFF

- [x] Contas e conta ativa.
- [x] MovimentaÃ§Ãµes do dia.
- [x] Saldo da conta.
- [x] Extrato com filtro e paginaÃ§Ã£o.
- [x] Consulta de perfil e alteraÃ§Ã£o de senha.
- [x] Onboarding PF/PJ.
- [x] Consentimentos de onboarding.
- [x] Upload e status de documentos KYC.
- [x] Pix por dados bancÃ¡rios e QR Code.
- [x] Pix por chave.
- [x] CobranÃ§as e gestÃ£o de chaves Pix.
- [x] Consulta de boleto.
- [x] Pagamento de boleto.
- [x] Recarga de celular.
- [x] Consulta e pagamento de dÃ©bitos veiculares.
- [x] TED e transferÃªncia interna.
- [x] Informe de rendimentos.
- [x] Open Finance: instituiÃ§Ãµes, criaÃ§Ã£o de consentimento e consulta de contas/transaÃ§Ãµes.
- [x] Open Finance: ciclo de vida de consentimentos, links, pagamentos, sweeping e Brick.
- [x] Produtos financeiros, cartÃµes, crÃ©dito e Escrow por feature flag.
- [x] Suporte, tickets e notificaÃ§Ãµes.
- [x] SolicitaÃ§Ã£o de encerramento da conta.

### CritÃ©rios de aceite do BFF

- frontend web executa os principais fluxos sem mocks;
- todo endpoint autenticado rejeita sessÃ£o invÃ¡lida e conta nÃ£o autorizada;
- erros possuem envelope seguro, cÃ³digo estÃ¡vel e correlation ID;
- mutaÃ§Ãµes aceitam e propagam `Idempotency-Key`;
- nenhum segredo Celcoin aparece em response, bundle ou log;
- testes de integraÃ§Ã£o cobrem sucesso, erro, timeout, repetiÃ§Ã£o e acesso negado.

## 6. P0 â€” SeguranÃ§a do backend

### 6.1 Webhooks

- [x] Remover comportamento fail-open quando o segredo estiver ausente.
- [x] Em staging/production, falhar na inicializaÃ§Ã£o ou rejeitar webhooks sem
      configuraÃ§Ã£o vÃ¡lida.
- [x] Permitir configuraÃ§Ã£o sem segredo somente em perfil de teste explÃ­cito.
- [x] Validar assinatura em tempo constante.
- [x] Tratar timestamp invÃ¡lido sem retornar erro interno.
- [x] Validar janela antirreplay.
- [x] Validar limite de payload antes do processamento.
- [x] Deduplicar por identificador externo.
- [x] Testar assinatura ausente, invÃ¡lida, expirada e repetida.

### 6.2 AdministraÃ§Ã£o

- [x] Proteger `GET /admin/webhooks`.
- [x] Proteger `POST /admin/webhooks/{id}/retry`.
- [x] Exigir papel administrativo/operacional apropriado.
- [x] Auditar consultas e reprocessamentos.
- [x] Desabilitar endpoints administrativos por padrÃ£o no SDK ou condicionÃ¡-los
      a configuraÃ§Ã£o explÃ­cita.

### 6.3 IdempotÃªncia concorrente

- [ ] Impedir que duas requisiÃ§Ãµes executem a mesma operaÃ§Ã£o quando o registro
      estiver `STARTED`.
- [ ] Retornar estado `IN_PROGRESS`, conflito seguro ou replay apropriado.
- [ ] Avaliar lock pessimista, lock otimista ou lease transacional.
- [ ] Definir timeout e recuperaÃ§Ã£o de registros abandonados.
- [ ] Definir polÃ­tica para registros `FAILED`.
- [ ] Criar teste concorrente com duas requisiÃ§Ãµes simultÃ¢neas.
- [ ] Confirmar que somente uma chamada externa Ã© realizada.

### 6.4 Hardening

- [ ] Adicionar rate limiting no BFF por usuÃ¡rio, conta, IP e operaÃ§Ã£o.
- [ ] Configurar CORS por ambiente.
- [ ] Aplicar headers HTTP de seguranÃ§a.
- [ ] Limitar tamanho de bodies e uploads.
- [ ] Implementar validaÃ§Ã£o centralizada.
- [ ] Executar secret scan e dependency scan.
- [ ] Revisar mascaramento de logs e auditoria.
- [ ] Preparar pentest web, API e mobile.

## 7. P1 â€” IntegraÃ§Ã£o real em sandbox/staging

- [ ] Provisionar credenciais e certificados em secret store.
- [ ] Configurar ambiente Celcoin sandbox autorizado.
- [ ] Validar OAuth, cache e renovaÃ§Ã£o de token.
- [ ] Validar mTLS com certificados reais do ambiente.
- [ ] Validar webhooks reais e rotaÃ§Ã£o de segredo.
- [ ] Preparar massa isolada e resetÃ¡vel.
- [ ] Executar jornada real de contas e saldo.
- [ ] Executar Pix de sandbox e receber o webhook correspondente.
- [ ] Executar consulta/pagamento de boleto.
- [ ] Executar recarga e dÃ©bitos veiculares.
- [ ] Executar TED quando o produto estiver habilitado.
- [ ] Validar Open Finance e redirects HTTPS.
- [ ] Conciliar operaÃ§Ã£o, webhook, status e extrato.
- [ ] Arquivar evidÃªncias por cenÃ¡rio e ambiente.

### CritÃ©rios de aceite de sandbox

- operaÃ§Ã£o criada pelo BFF chega Ã  Celcoin uma Ãºnica vez;
- status final Ã© sincronizado por consulta ou webhook;
- frontend exibe resultado e comprovante corretos;
- timeout e resposta incerta nÃ£o duplicam a operaÃ§Ã£o;
- massa de teste pode ser restaurada para nova execuÃ§Ã£o;
- logs permitem rastrear a operaÃ§Ã£o sem revelar dados sensÃ­veis.

## 8. P1 â€” FinalizaÃ§Ã£o do frontend web

### 8.1 IntegraÃ§Ã£o

- [ ] Substituir mocks pelos contratos reais do BFF em staging.
- [ ] Manter mocks somente para testes isolados e desenvolvimento controlado.
- [ ] Centralizar tratamento de erro HTTP e sessÃ£o expirada.
- [ ] Invalidar caches apÃ³s mutaÃ§Ãµes e webhooks/push.
- [ ] Implementar feature flags por produto e ambiente.

### 8.2 Fluxos financeiros

- [ ] Adicionar revisÃ£o antes de Pix, boleto, recarga, dÃ©bitos e TED.
- [ ] Mostrar favorecido, conta, valor, tarifa e data antes da confirmaÃ§Ã£o.
- [ ] Implementar confirmaÃ§Ã£o/step-up quando exigido.
- [ ] Exibir sucesso, processamento, falha e resultado incerto.
- [ ] Implementar comprovantes com download/compartilhamento HTTPS seguro.
- [ ] Impedir duplo clique e reutilizar idempotÃªncia corretamente.

### 8.3 KYC e perfil

- [ ] Finalizar upload de documentos com progresso e retry.
- [ ] Implementar captura/permissÃµes sem expor arquivo sensÃ­vel.
- [ ] Exibir status KYC pendente, aprovado e recusado.
- [ ] Finalizar ediÃ§Ã£o cadastral e troca de senha.
- [ ] Aplicar mÃ¡scaras de CPF, CNPJ, telefone, CEP e moeda BRL.

### 8.4 Responsividade e UI

- [ ] Corrigir altura do card de recarga no mobile.
- [ ] Garantir que â€œRevisar recargaâ€ fique visÃ­vel.
- [ ] Remover sobreposiÃ§Ã£o com â€œDÃ©bitos veicularesâ€.
- [ ] Confirmar novos formulÃ¡rios de Cadastro, Perfil, ServiÃ§os e Suporte no
      desktop e notebook.
- [ ] Validar menu â€œMaisâ€ no tablet.
- [ ] Testar 375, 430, 600, 768, 900, 1023, 1024, 1280, 1440 e 1920 px.
- [ ] Garantir ausÃªncia de overflow horizontal.
- [ ] Revisar contraste, foco, teclado, zoom 200% e alvos de toque de 44 px.
- [ ] Regenerar capturas viewport e full-page apÃ³s cada mudanÃ§a visual.

### CritÃ©rios de aceite do web

- todos os fluxos principais usam o BFF de staging;
- nenhuma operaÃ§Ã£o financeira Ã© executada sem revisÃ£o e idempotÃªncia;
- nenhuma tela apresenta truncamento, sobreposiÃ§Ã£o ou scroll horizontal;
- navegaÃ§Ã£o completa funciona por mouse, teclado e toque;
- loading, vazio, erro, sucesso, retry e sessÃ£o expirada estÃ£o cobertos;
- build de produÃ§Ã£o e E2E web passam.

## 9. P1/P2 â€” Testes e qualidade

### Backend

- [ ] Reparar/substituir o Maven Wrapper corrompido.
- [ ] Executar `./mvnw clean verify`.
- [ ] Manter Spotless e Maven Enforcer verdes.
- [ ] Confirmar e elevar o gate JaCoCo de forma sustentÃ¡vel.
- [ ] Testar migraÃ§Ã£o em banco vazio.
- [ ] Testar upgrade a partir de cada versÃ£o suportada.
- [ ] Ampliar testes de mÃ³dulos com pouca cobertura.
- [ ] Criar testes BFF â†’ SDK â†’ WireMock.
- [ ] Criar testes com PostgreSQL/Testcontainers.

### Frontend web

- [ ] Executar `npm ci`.
- [ ] Executar `npm run validate`.
- [ ] Executar `npm run web:build`.
- [ ] Executar `npm run test:e2e:web`.
- [ ] Manter o secret scan verde.
- [ ] Cobrir encerramento de conta.
- [ ] Cobrir upload e status de KYC.
- [ ] Cobrir comprovantes e informe de rendimentos.
- [ ] Cobrir feature flags.
- [ ] Adicionar regressÃ£o visual automatizada.
- [ ] Executar E2E contra staging alÃ©m do BFF mockado.

## 10. P2 â€” Observabilidade e operaÃ§Ã£o

- [ ] Implementar logs estruturados com correlation ID.
- [ ] Adicionar mÃ©tricas de latÃªncia, erro, timeout e rate limit.
- [ ] Adicionar tracing BFF â†’ SDK â†’ Celcoin.
- [ ] Criar alertas para falha de autenticaÃ§Ã£o, webhook, conciliaÃ§Ã£o e operaÃ§Ã£o
      com resultado incerto.
- [ ] Criar dashboard operacional.
- [ ] Definir SLOs e SLIs.
- [ ] Configurar backup e restauraÃ§Ã£o do PostgreSQL.
- [ ] Testar restauraÃ§Ã£o de desastre.
- [ ] Definir retenÃ§Ã£o de auditoria e dados pessoais.
- [ ] Criar runbooks de indisponibilidade da Celcoin e reprocessamento.
- [ ] Definir deploy canÃ¡rio/gradual e rollback.

## 11. P3 â€” iOS

- [ ] Executar as suÃ­tes Detox existentes em simulador.
- [ ] Executar novamente contra BFF de staging e sandbox.
- [ ] Provisionar massa isolada para autenticaÃ§Ã£o, KYC, Pix e pagamentos.
- [ ] Finalizar permissÃµes de cÃ¢mera, biometria e notificaÃ§Ãµes.
- [ ] Validar Keychain e limpeza da sessÃ£o.
- [ ] Testar deep links e retornos de Open Finance.
- [ ] Testar diferentes iPhones e iPads suportados.
- [ ] Validar acessibilidade com VoiceOver.
- [ ] Configurar certificados, provisioning profiles e assinatura.
- [ ] Criar pipeline de build e distribuiÃ§Ã£o.
- [ ] Publicar no TestFlight.
- [ ] Executar pentest mobile.
- [ ] Preparar privacidade, screenshots e metadados da App Store.
- [ ] Submeter para aprovaÃ§Ã£o somente apÃ³s os gates anteriores.

## 12. P3 â€” Android

- [ ] Implementar/executar E2E com Detox ou Appium.
- [ ] Executar contra BFF de staging e sandbox.
- [ ] Validar Android Keystore e armazenamento seguro.
- [ ] Finalizar permissÃµes de cÃ¢mera, biometria e notificaÃ§Ãµes.
- [ ] Testar App Links e retornos de Open Finance.
- [ ] Testar aparelhos, densidades e versÃµes Android suportadas.
- [ ] Validar acessibilidade com TalkBack.
- [ ] Configurar assinatura e secret store.
- [ ] Criar pipeline AAB.
- [ ] Publicar no Play Internal Testing.
- [ ] Executar pentest mobile.
- [ ] Preparar Data Safety, polÃ­tica de privacidade e ficha da Play Store.
- [ ] Submeter para aprovaÃ§Ã£o somente apÃ³s os gates anteriores.

## 13. Ordem recomendada de execuÃ§Ã£o

1. Reparar os comandos locais e confirmar CI atual.
2. Corrigir webhook fail-open e proteger endpoints administrativos.
3. Corrigir concorrÃªncia da idempotÃªncia.
4. Implementar a fundaÃ§Ã£o do BFF e autenticaÃ§Ã£o.
5. Implementar autorizaÃ§Ã£o por conta e contratos bÃ¡sicos.
6. Integrar dashboard, saldo e extrato ao BFF real.
7. Integrar Pix e pagamentos com confirmaÃ§Ã£o e comprovante.
8. Concluir KYC, perfil, serviÃ§os e suporte.
9. Corrigir as pendÃªncias responsivas do web.
10. Executar E2E web contra staging/sandbox.
11. Implementar observabilidade, conciliaÃ§Ã£o e runbooks.
12. Concluir e validar iOS.
13. Concluir e validar Android.
14. Executar pentest e homologaÃ§Ã£o.
15. Realizar publicaÃ§Ã£o gradual somente com todos os gates aprovados.

## 14. DefiniÃ§Ã£o de pronto por tarefa

Uma tarefa somente pode ser marcada como concluÃ­da quando:

- [ ] implementaÃ§Ã£o estÃ¡ modular e sem duplicaÃ§Ã£o desnecessÃ¡ria;
- [ ] validaÃ§Ãµes e erros seguros foram tratados;
- [ ] testes unitÃ¡rios relevantes foram adicionados/atualizados;
- [ ] teste de integraÃ§Ã£o foi incluÃ­do quando existe fronteira externa;
- [ ] lint, formataÃ§Ã£o, typecheck e build relevantes passam;
- [ ] nenhuma credencial ou dado sensÃ­vel foi introduzido;
- [ ] documentaÃ§Ã£o foi atualizada;
- [ ] riscos e limitaÃ§Ãµes restantes foram declarados;
- [ ] evidÃªncia real foi arquivada quando a tarefa envolve E2E ou sandbox.

## 15. Comandos de validaÃ§Ã£o

### Backend

```bash
./mvnw -B -ntp spotless:check
./mvnw -B -ntp clean verify
```

### Frontend

```bash
cd frontend
npm ci
npm run validate
npm run web:build
npm run test:e2e:web
```

### Capturas responsivas

```bash
cd frontend
npm run capture:responsive:web
```

## 16. Formato esperado do relatÃ³rio do Codex

Ao concluir qualquer bloco deste roadmap, responder com:

1. resultado alcanÃ§ado;
2. arquivos alterados;
3. decisÃµes tÃ©cnicas tomadas;
4. testes executados e resultados;
5. critÃ©rios de aceite validados;
6. riscos ou pendÃªncias restantes;
7. prÃ³xima tarefa recomendada;
8. confirmaÃ§Ã£o de que nenhum commit, push ou deploy foi realizado, salvo se
   explicitamente autorizado.
