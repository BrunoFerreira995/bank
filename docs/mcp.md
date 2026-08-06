# MCP da Celcoin

O SDK não implementa um servidor MCP proprietário/oficial da Celcoin. Como
adaptador local, porém, ele oferece um endpoint MCP JSON-RPC opt-in em
`/mcp`, desabilitado por padrão (`CELCOIN_MCP_ENABLED=false`). O adaptador
expõe somente operações já existentes no SDK:

- `pix_participants`;
- `pix_decode_emv`;
- `account_balance`.

Ele implementa `initialize`, `notifications/initialized`, `tools/list` e
`tools/call`. O endpoint é uma ponte de integração do SDK, não uma afirmação
de compatibilidade com um catálogo MCP oficial Celcoin.

Antes de habilitá-lo, a aplicação hospedeira deve protegê-lo com autenticação,
autorização e controles de auditoria. Também deverá:

- autenticar com o mesmo `CelcoinTokenService`;
- publicar somente operações habilitadas para o parceiro;
- exigir idempotência em operações financeiras;
- mascarar CPF/CNPJ, tokens, cartões e certificados;
- registrar consentimento, auditoria, correlation ID e resultado da operação;
- nunca expor credenciais ou permitir que o modelo escolha URLs arbitrárias.

Quando a Celcoin fornecer um catálogo MCP oficial, ele deverá ser integrado
atrás de uma interface própria e validado com contrato WireMock, sem alterar
os clientes REST existentes.
