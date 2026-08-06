# Matriz E2E — cobertura de 100%

Cada linha precisa ser executada em Android e iOS, em staging e sandbox, com
resultado, versão do app, versão do BFF, dispositivo e evidência arquivados.

| Domínio | Fluxos obrigatórios | Cenários de falha/segurança |
|---|---|---|
| Fundação | inicialização, feature flags, deep link, atualização e logout | app sem rede, timeout, sessão expirada |
| Identidade | login PF/PJ, recuperação, troca de senha, MFA, biometria | credencial inválida, MFA recusado, dispositivo incompatível |
| KYC | onboarding PF/PJ, consentimentos, documentos, status aprovado/pendente/recusado | permissão negada, upload interrompido, liveness recusado |
| Conta | dashboard, troca de conta, saldo, extrato, filtros, perfil | conta bloqueada, saldo indisponível, retry |
| Pix | chave, dados bancários, QR, cobrança, devolução, chaves, portabilidade | QR inválido, duplicidade, bloqueio cautelar, análise |
| Boletos | consulta, autorização, pagamento, cancelamento, comprovante | vencido, indisponível, pagamento em análise |
| Recargas | operador, produto, pagamento, status e reprocessamento | falha de operador, retry idempotente, valor inválido |
| Débitos | consulta veicular, seleção, pagamento e comprovante | débito vencido, documento inválido, indisponibilidade |
| Cartões | solicitação, ativação, bloqueio, limite, fatura e transações | PAN/CVV nunca visíveis integralmente |
| Crédito | simulação, proposta, documentos, consignado e portabilidade | inelegível, recusado, análise pendente |
| Escrow | saldo, partes, eventos e estados autorizados | estado não autorizado, bloqueio, timeout |
| Open Finance | instituição, consentimento, vínculo, redirect, pagamentos e revogação | callback expirado, timeout, recusa, duplicidade |
| Operação | push, central, FAQ, ticket e status de serviços | payload adulterado não altera saldo |
| Web | login, navegação, deep link e pagamentos no browser | fallback de Keychain, refresh e sessão expirada |

## Critério de aprovação

Um domínio só é considerado 100% quando todos os fluxos da linha passam nos
dois sistemas operacionais, nos dois ambientes, com massa isolada e evidências
arquivadas. Falha intermitente, cobertura apenas unitária ou execução somente
no simulador não fecha o critério.
