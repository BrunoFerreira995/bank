# Checklist de Evidências

Copie este bloco para cada cenário e exporte o documento final para PDF.

| Campo | Preenchimento |
|---|---|
| ID do cenário | `<AUTH-01>` |
| Produto | `<PRODUTO>` |
| Data/hora com timezone | `<AAAA-MM-DD HH:mm:ss -03:00>` |
| Usuário/conta de teste | `<IDENTIFICADOR INTERNO>` |
| Passos executados | `<PASSOS>` |
| Resultado esperado | `<DESCRIÇÃO>` |
| Resultado observado | `<DESCRIÇÃO>` |
| Identificador Celcoin | `<transactionId/clientRequestId/etc.>` |
| Webhook recebido | `<SIM/NÃO; entity; timestamp>` |
| Evidência anexada | `<arquivo>` |
| Status | `<PASSOU/REPROVADO/BLOQUEADO>` |
| Observação/protocolo | `<DETALHES>` |

## Critérios de aceite

- A jornada é iniciada e concluída pela aplicação integrada.
- O identificador aparece na tela ou no log sanitizado.
- Status final e tratamento de erro são compatíveis com o contrato.
- Webhooks são processados uma única vez, inclusive em reenvios.
- Dados sensíveis e credenciais não aparecem nas evidências.
- Todos os cenários contratados possuem evidência ou justificativa.
