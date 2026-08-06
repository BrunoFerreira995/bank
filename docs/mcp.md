# MCP da Celcoin

O repositório não implementa um servidor MCP proprietário da Celcoin porque
não foi fornecido um protocolo, catálogo de ferramentas ou endpoint oficial
para esse serviço. Expor um MCP inventando ferramentas seria incompatível com
os contratos REST já validados.

O SDK pode ser usado como camada de domínio por um servidor MCP externo. Esse
servidor deverá:

- autenticar com o mesmo `CelcoinTokenService`;
- publicar somente operações habilitadas para o parceiro;
- exigir idempotência em operações financeiras;
- mascarar CPF/CNPJ, tokens, cartões e certificados;
- registrar consentimento, auditoria, correlation ID e resultado da operação;
- nunca expor credenciais ou permitir que o modelo escolha URLs arbitrárias.

Quando a Celcoin fornecer um catálogo MCP oficial, a integração deve ser
adicionada atrás de uma interface própria e validada com contrato WireMock,
sem alterar os clientes REST existentes.
