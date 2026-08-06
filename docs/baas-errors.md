# Tabela de Erros BaaS

O SDK disponibiliza o catálogo central em `CelcoinBaasErrors`. Cada entrada
contém o código remoto, mensagem, módulos afetados, indicação de retentativa e
ação recomendada:

```java
var error = CelcoinBaasErrors.find("CBE100");
error.modules();    // pix, internal-transfer
error.retryable();  // true: aguarde e consulte o status
error.action();
```

Respostas HTTP que contenham `error.errorCode` são convertidas em
`CelcoinBaasException`, preservando `remoteCode`, `correlationId`,
`remoteRequestId` e o descritor do catálogo. Códigos ainda não publicados no
SDK retornam um descritor seguro como `unknown`, sem esconder a mensagem
original da exceção.

O catálogo cobre os fluxos de contas, relatórios, Pix e transferências entre
contas, incluindo validações, duplicidade, saldo insuficiente, bloqueios,
contas encerradas e falhas transitórias. Os catálogos específicos de boleto,
recarga e veículo continuam disponíveis nas respectivas operações.
