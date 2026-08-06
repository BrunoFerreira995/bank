# Tabela de erros — cel_credit

As respostas HTTP de erro da Plataforma de Crédito devem ser tratadas pelo
status HTTP e pelo corpo retornado pela API. O SDK preserva o payload no erro
de integração para permitir auditoria sem expor credenciais ou dados sensíveis.

| Situação | Tratamento recomendado |
| --- | --- |
| `401 Unauthorized` | Renovar o token OAuth2 e repetir a autenticação. |
| `403 Forbidden` | Verificar se o produto, o originador e a operação estão contratados. |
| `404 Not Found` | Confirmar o identificador da pessoa, empresa, produto ou solicitação. |
| `422 Unprocessable Entity` | Corrigir os campos de negócio e documentos enviados. |
| `5xx` | Registrar correlação, aguardar e aplicar retry com backoff. |

Na originação, mudanças de estado e validação de documentos são assíncronas;
acompanhe `APPLICATION_STATUS_UPDATED`, `PERSON_DOCUMENT_STATUS_UPDATED` e
`BUSINESS_DOCUMENT_STATUS_UPDATED` por webhook.
