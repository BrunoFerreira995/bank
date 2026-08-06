package com.brunopedraca.celcoin.common.error;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Catálogo central dos códigos funcionais publicados nas APIs BaaS. */
public final class CelcoinBaasErrors {
    public record Error(String code, String message, Set<String> modules, boolean retryable, String action) {}

    private CelcoinBaasErrors() {}

    public static List<Error> all() {
        return List.of(
                e("CBE001", "ClientCode é obrigatório", "pix", false, "Informe um identificador externo"),
                e("CBE007", "fullName é obrigatório e deve ser completo", "accounts", false, "Complete o nome"),
                e("CBE008", "birthDate é obrigatório ou inválido", "accounts", false, "Envie a data no formato oficial"),
                e("CBE009", "address é obrigatório", "accounts", false, "Informe o endereço"),
                e("CBE022", "Já existe uma conta vinculada ao documento", "accounts", false, "Consulte a conta existente"),
                e("CBE028", "Credenciais inválidas", "pix,accounts", false, "Revise as credenciais e o token"),
                e("CBE039", "Account inválido ou não localizado", "accounts,reports,pix", false, "Confirme a conta"),
                e("CBE040", "DocumentNumber inválido", "accounts,reports", false, "Confirme o documento"),
                e("CBE041", "Account possui tamanho máximo de 20 caracteres", "accounts,reports,pix", false, "Use uma conta válida"),
                e("CBE042", "DocumentNumber possui tamanho máximo de 14 caracteres", "accounts,reports", false, "Use um documento válido"),
                e("CBE066", "Limite da consulta deve estar entre 1 e 200", "reports", false, "Ajuste o limite"),
                e("CBE067", "Página informada não contém contas", "accounts", false, "Consulte outra página"),
                e("CBE068", "dateFrom não pode ser maior que dateTo", "reports", false, "Corrija o intervalo"),
                e("CBE073", "Informe account ou documentNumber", "accounts,reports", false, "Envie um identificador"),
                e("CBE079", "Intervalo da consulta não pode ultrapassar 7 dias", "accounts,reports", false, "Reduza o intervalo"),
                e("CBE080", "Page inválido", "accounts,reports", false, "Use uma página positiva"),
                e("CBE088", "Limit inválido", "accounts,reports", false, "Use um limite entre 1 e 200"),
                e("CBE089", "Consulta não permitida; conta bloqueada", "accounts,reports", false, "Desbloqueie a conta ou trate o bloqueio"),
                e("CBE090", "Consulta não permitida; conta encerrada", "accounts,reports", false, "Use uma conta ativa"),
                e("CBE091", "Informe id, clientCode ou endtoendId", "pix", false, "Envie um identificador da transação"),
                e("CBE094", "amount é obrigatório", "pix,internal-transfer", false, "Informe um valor"),
                e("CBE095", "amount inválido; deve ser maior que zero", "pix,internal-transfer", false, "Corrija o valor"),
                e("CBE100", "Existe um lançamento idêntico pendente", "pix,internal-transfer", true, "Aguarde e consulte o status"),
                e("CBE101", "Já existe lançamento com o mesmo clientCode", "pix,internal-transfer", false, "Consulte a operação ou use novo identificador"),
                e("CBE102", "Valor ultrapassa o limite permitido por operação", "internal-transfer", false, "Reduza o valor"),
                e("CBE123", "Saldo insuficiente", "internal-transfer", false, "Adicione saldo à conta"),
                e("CBE150", "Informe id, clientCode ou endtoendId", "pix", false, "Envie um identificador da transação"),
                e("CBE151", "Nenhum lançamento localizado para o período", "reports", false, "Revise o período consultado"),
                e("CBE153", "dateFrom e dateTo são obrigatórios", "reports", false, "Informe o intervalo"),
                e("CBE173", "keyType é obrigatório e deve ser válido", "pix", false, "Use CPF, CNPJ, EMAIL, PHONE ou EVP"),
                e("CBE174", "key não pode ultrapassar 77 caracteres", "pix", false, "Corrija o tamanho da chave"),
                e("CBE175", "Chave Pix inválida ou não permitida", "pix", false, "Revise a chave"),
                e("CBE176", "Operação não permitida; conta encerrada", "pix", false, "Use uma conta ativa"),
                e("CBE177", "Operação não permitida; conta bloqueada", "pix", false, "Trate o bloqueio da conta"),
                e("CBE187", "Limite de chaves Pix excedido para conta PF", "pix", false, "Exclua uma chave existente"),
                e("CBE188", "Limite de chaves Pix excedido para conta PJ", "pix", false, "Exclua uma chave existente"),
                e("CBE190", "Chave não está vinculada à conta", "pix", false, "Use a conta correta"),
                e("CBE224", "Formato do JSON fora do padrão", "pix", false, "Revise o payload"),
                e("CBE234", "Não foi possível realizar a operação", "pix,accounts", true, "Consulte o status e tente novamente"),
                e("CBE261", "clientRequestId é obrigatório", "internal-transfer", false, "Informe um identificador único"),
                e("CBE286", "Claim não permitido para chave EVP", "pix", false, "Use o fluxo compatível com a chave"),
                e("CBE287", "Claim não permitido para chave CPF/CNPJ", "pix", false, "Use o fluxo compatível com a chave"),
                e("CBE308", "clientRequestId excede 200 caracteres", "internal-transfer", false, "Use um identificador menor"),
                e("CBE312", "Transferência para a mesma conta não permitida", "internal-transfer", false, "Informe outra conta"),
                e("CBE314", "Conta de origem não localizada", "internal-transfer", false, "Confirme a conta de origem"),
                e("CBE315", "Conta de destino não localizada", "internal-transfer", false, "Confirme a conta de destino"),
                e("CBE328", "Cliente da conta de crédito está inativo", "internal-transfer", false, "Ative o cliente"),
                e("CBE376", "Diferença entre datas não pode ultrapassar 7 dias", "reports", false, "Reduza o intervalo"),
                e("CBE669", "Cliente possui restrição no BC Protege+", "accounts", false, "Retire a restrição e tente novamente"),
                e("LC003", "Chave Pix inválida para esta ação", "pix", false, "Confirme a chave e contate o suporte se necessário"),
                e("CIE999", "Ocorreu um erro interno durante a chamada", "all", true, "Consulte o correlationId e tente novamente"));
    }

    public static Error find(String code) {
        String normalized = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        return all().stream().filter(error -> error.code().equals(normalized)).findFirst()
                .orElse(new Error(code, "Erro BaaS não mapeado", Set.of("unknown"), false, "Consulte a mensagem original da Celcoin"));
    }

    private static Error e(String code, String message, String modules, boolean retryable, String action) {
        return new Error(code, message, Set.of(modules.split(",")), retryable, action);
    }
}
