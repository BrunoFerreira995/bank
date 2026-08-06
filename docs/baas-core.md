# cel_banking — BaaS & Core Banking

O módulo `accounts()` reúne as operações de conta, saldo, extrato, transferência
interna, bloqueios, monitoramento, informe de rendimentos e fluxos de abertura
Core Banking. Os produtos BaaS e Core usam a mesma infraestrutura de APIs; a
principal diferença é a licença e o fluxo de onboarding.

Além do extrato convencional, `accounts()` oferece `getDetailedStatement` para
o extrato detalhado e `getStatementTransaction` para consultar uma transação por
`id`, `clientCode`, `endToEndId`, `returnIdentification` ou `movementType`.

## Mapa de responsabilidades

| Área | Acesso no SDK | Dependência |
|---|---|---|
| Contas e KYC | `onboarding()` / `accounts()` | produto contratado |
| Saldo e extrato | `accounts()` | conta ativa |
| Pix, TED e boletos | `pix()`, `accounts()`, `boletos()` | conta e limites |
| Informe de rendimentos | `accounts()` | ano-calendário disponível |
| Follow the Money | configuração Celcoin/Painel | contratação e implantação |
| MyBenk | canais white-label Celcoin | contratação separada |
| Painel do Cliente | portal administrativo Celcoin | usuário master |

## FAQs

### BaaS e Core Banking usam APIs diferentes?

Não em regra. A documentação Celcoin informa que as APIs, exceto o fluxo de
onboarding, atendem BaaS e Core Banking. O que muda é a licença, a titularidade
da infraestrutura e os produtos habilitados.

### Posso usar saldo de sandbox em produção?

Não. `accounts().addSandboxBalance(...)` é exclusivo de sandbox. Em produção,
o saldo deve ser originado pelos fluxos financeiros contratados, como cash-in,
Pix ou TED.

### O SDK substitui o Painel do Cliente?

Não. O SDK cobre APIs públicas. Configurações operacionais, whitelist, limites,
usuários administrativos e análises de risco permanecem no Painel quando esse
produto estiver contratado.

### Como funciona o Follow the Money?

É o motor de antifraude e PLD da Celcoin. As decisões podem autorizar, bloquear
ou colocar a transação em bloqueio cautelar. A configuração de regras, usuários
e relatórios é feita pela operação habilitada da Celcoin; o contrato público
consultado não fornece endpoints REST para o SDK gerenciar essas regras.

### Como devo tratar webhooks?

Receba, persista e deduplicate eventos por identificador externo. Use o extrato
ou o endpoint de status como fonte de confirmação operacional; webhook é um
gatilho transacional e pode ser reenviado.

### O que fazer quando uma operação retorna erro?

Registre status HTTP, código/mensagem remotos, correlation ID, request ID e o
identificador da operação. Não faça retry automático de operações não idempotentes
sem uma chave de idempotência.

## Termos de uso e segurança operacional

- Use apenas credenciais do ambiente correspondente e mantenha secrets fora do
  repositório.
- Envie CPF/CNPJ e dados bancários somente nos campos exigidos pelo contrato.
- Mascare documentos, tokens, números de cartão e certificados nos logs.
- Separe sandbox, homologação e produção; não reutilize massa de teste em
  produção.
- Respeite limites, janelas operacionais e regras de produto informadas pela
  Celcoin durante a implantação.
- O parceiro é responsável pela experiência do usuário, consentimentos,
  conciliação e decisão final em fluxos de risco quando aplicável.

## APIs e componentes preparados

As operações públicas já implementadas estão descritas em:

- [banking.md](banking.md)
- [onboarding.md](onboarding.md)
- [ted.md](ted.md)
- [income-report.md](income-report.md)
- [webhooks.md](webhooks.md)

Follow the Money, MyBenk, Painel do Cliente, gerenciador de webhooks Celcoin e
relatórios detalhados dependem de habilitação/contrato específico e não são
simulados por endpoints inventados no SDK.
