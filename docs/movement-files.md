# Arquivos de movimentação via SFTP

O SDK fornece `SftpMovementFileClient` para baixar arquivos de movimentação
disponibilizados pela Celcoin. O adaptador usa o cliente OpenSSH do sistema,
exige `BatchMode`, valida a chave do servidor por `known_hosts` e retorna
tamanho e SHA-256 do arquivo recebido.

```java
var files = new SftpMovementFileClient(
    "sftp.celcoin.example", 22, "partner",
    Path.of("/etc/celcoin/sftp/id_ed25519"),
    Path.of("/etc/celcoin/sftp/known_hosts"),
    Duration.ofMinutes(2));

var result = files.download(new SftpDownloadRequest(
    "/out/movement-2026-08-06.txt", "/var/lib/celcoin/movement-2026-08-06.txt"));
```

Requisitos operacionais:

- instalar `sftp`/OpenSSH no host da aplicação;
- armazenar a chave privada fora do repositório e com permissão restrita;
- receber o `known_hosts` por canal seguro e não usar `StrictHostKeyChecking=no`;
- validar o SHA-256 e processar o arquivo de forma idempotente antes de
  persistir os lançamentos;
- não registrar o conteúdo do arquivo ou credenciais nos logs.

Os nomes, diretórios e janela de disponibilização dos arquivos dependem do
contrato SFTP do parceiro com a Celcoin.
