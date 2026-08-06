package com.brunopedraca.celcoin.itp;

import com.brunopedraca.celcoin.itp.CelcoinItpDtos.ErrorDescriptor;
import java.util.Map;

public final class ItpPaymentErrors {
    private ItpPaymentErrors() {}
    private static final Map<String, ErrorDescriptor> ERRORS = Map.ofEntries(
            Map.entry("SALDO_INSUFICIENTE", new ErrorDescriptor("SALDO_INSUFICIENTE", "Saldo insuficiente", true, "Tentar novamente intradia após recompor saldo")),
            Map.entry("FALHA_INFRAESTRUTURA", new ErrorDescriptor("FALHA_INFRAESTRUTURA", "Falha de infraestrutura", true, "Aguardar e tentar novamente")),
            Map.entry("NAO_INFORMADO", new ErrorDescriptor("NAO_INFORMADO", "Motivo não informado", true, "Tratar como falha transitória")),
            Map.entry("CONSENTIMENTO_INVALIDO", new ErrorDescriptor("CONSENTIMENTO_INVALIDO", "Consentimento inválido", false, "Criar novo consentimento")),
            Map.entry("VALOR_INVALIDO", new ErrorDescriptor("VALOR_INVALIDO", "Valor inválido", false, "Corrigir o valor")),
            Map.entry("PAGAMENTO_DIVERGENTE_CONSENTIMENTO", new ErrorDescriptor("PAGAMENTO_DIVERGENTE_CONSENTIMENTO", "Pagamento divergente", false, "Criar novo consentimento")),
            Map.entry("CONTA_DESTINO_INVALIDA", new ErrorDescriptor("CONTA_DESTINO_INVALIDA", "Conta destino inválida", false, "Corrigir dados do recebedor")));
    public static ErrorDescriptor describe(String code) {
        return ERRORS.getOrDefault(code, new ErrorDescriptor(code, "Erro de pagamento ITP", false, "Consultar o detalhe retornado pela Celcoin"));
    }
}
