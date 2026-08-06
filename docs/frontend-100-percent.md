# Definição de 100% do frontend

O frontend considera um fluxo pronto somente quando possui:

- estado de carregamento, vazio, erro com mensagem segura, sucesso e retry;
- chamada exclusivamente ao BFF, idempotência nas mutações, confirmação e comprovante;
- nenhum segredo Celcoin, credencial, PAN/CVV ou dado pessoal em bundle/log;
- callback refletido por consulta ao BFF ou push que apenas invalida/sincroniza cache;
- testes unitários/componentes e contrato versionado por ambiente;
- evidência de acessibilidade, segurança, homologação e release.

O gate local é executado por `npm run verify:definition` e também dentro de
`npm run validate`. E2E em staging/sandbox, pentest, distribuição nas lojas e
aprovação de produção são gates externos e exigem evidência operacional.
