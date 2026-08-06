# FAQ — Abertura de Contas KYC

### Preciso enviar os documentos por um endpoint separado?

Não. No fluxo sem WebView, envie URLs públicas em `files[]` na proposta. No
fluxo com WebView, compartilhe o link recebido no webhook de onboarding.

### Posso cancelar uma proposta?

Não. Depois de criada, a proposta deve seguir até aprovação ou reprovação. Se o
cliente desistir, não compartilhe o WebView; não crie uma nova proposta para o
mesmo documento dentro da janela de 24 horas.

### Quais documentos são obrigatórios?

PF: RG, CNH ou RNE e selfie. PJ: documentos do responsável, selfie e contrato
social. Para representante legal, inclua também a procuração de poderes. MEI
usa o certificado correspondente e a documentação do sócio.

### Como simulo resultados no sandbox?

Use telefone PF ou contato PJ terminado em `1`, `2` ou `3`: `1` aprova, `2`
reprova a primeira etapa e `3` aprova a primeira e reprova a segunda. Outros
finais seguem o comportamento próximo do fluxo real.

### O SDK consulta o BC Protege+?

Não diretamente. A verificação é obrigatória, mas executada internamente pela
Celcoin no processo de abertura; o SDK acompanha o resultado pelos status e
webhooks de onboarding.

### Como acompanho uma proposta?

Persista o `proposalId` retornado na criação, processe os webhooks e use
`onboarding().getProposal(proposalId)` como consulta manual.

### O WebView é obrigatório?

Não para o fluxo em que a aplicação envia URLs públicas dos documentos. A selfie
e as verificações continuam obrigatórias conforme a política KYC contratada.
