# Erros das Embedded Solutions

| Produto | Códigos/documentação | Tratamento |
| --- | --- | --- |
| DDA | `CDDA001`, `CDDA101`, `CDDA115` | Validar documento, acompanhar o webhook e respeitar o limite de 20 documentos. |
| Pagamento de contas | `44`, `50`, `68`, `183`, `240`, `243`, `244`, `258`, `263`, `598` | Não repetir automaticamente sem consultar o status; usar reserva, captura e reversão conforme a etapa. |
| NFS-e | `CBE-INV-001` a `CBE-INV-007` | Corrigir payload, certificado, autorização ou cadastro da empresa. |
| Débito veicular | Catálogo `CelcoinVehicleErrors` | Respeitar dependências entre débitos e usar a chave de idempotência. |

DDA e NFS-e possuem etapas assíncronas. O status final deve ser obtido pelo
webhook ou endpoint de consulta, não apenas pelo retorno inicial da requisição.
