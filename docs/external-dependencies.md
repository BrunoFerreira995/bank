# Pendências externas do checklist

Os itens abaixo permanecem como `[~]` porque não podem ser implementados
corretamente apenas no SDK sem uma mudança de estado fora deste repositório.
Cada item já está refletido no checklist principal.

## Contrato ou produto Celcoin

- BC Protege+: execução automática pela Celcoin, sem endpoint dedicado no
  contrato SDK.
- Brick Bank transmissora/detentora e Brick Insurance receptora/transmissora:
  dependem de contratação, infraestrutura e APIs específicas não publicadas.
- Canais APP/Internet Banking whitelabel e APIs administrativas: dependem de
  contratação e configuração no Painel do Cliente.
- Jornada Pay by Link: depende da jornada whitelabel contratada.
- MCP oficial Celcoin: o SDK possui um adaptador local opt-in, mas não existe
  catálogo ou servidor oficial fornecido pela Celcoin.

## Portal e operação

- Portal, Hall, aplicações, catálogo, membros, menus e painéis Open Plus são
  funcionalidades do portal web, sem endpoints REST públicos no contrato do
  SDK.
- Credenciais de teste, publicação em produção e configurações Open Plus/Open
  Keys são provisionadas pelo portal e pelo contrato do parceiro.
- Dashboard, transações, relatórios, configurações e demos Open Keys dependem
  do produto habilitado no portal.
- Central de ajuda, SLA e tickets dependem do canal de suporte contratado e
  não possuem API pública mapeada.

## Validação local

- `./mvnw verify` passa com 145 testes, 0 falhas, 0 erros e cobertura global
  JaCoCo de 68,49% quando Docker e portas locais estão disponíveis.

Para transformar qualquer item externo em implementação, é necessário anexar
o contrato OpenAPI/SDK correspondente, credenciais e ambiente homologável, ou
fornecer acesso ao portal/produto contratado.
