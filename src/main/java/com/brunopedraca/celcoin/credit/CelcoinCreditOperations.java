package com.brunopedraca.celcoin.credit;

import java.util.Map;

public interface CelcoinCreditOperations {
    CelcoinCreditTokenResponse authenticate();

    Map<String, Object> createApplication(Map<String, Object> request);

    Map<String, Object> listApplications(CelcoinCreditApplicationQuery query);

    Map<String, Object> getApplication(String applicationId);

    Map<String, Object> simulate(String productId, Map<String, Object> request);

    Map<String, Object> listCustomVariables();

    Map<String, Object> createCustomVariable(Map<String, Object> request);

    Map<String, Object> updateCustomVariable(String variableId, Map<String, Object> request);

    void deleteCustomVariable(String variableId);

    Map<String, Object> getSignatures(String applicationId);

    Map<String, Object> sendTimestampSignature(String applicationId, Map<String, Object> request);

    /** Simula as condições econômicas de uma portabilidade do Crédito do Trabalhador. */
    Map<String, Object> simulatePortability(String productId, Map<String, Object> request);

    /** Solicita o termo de autorização para consulta do vínculo empregatício. */
    Map<String, Object> requestPortabilityAuthorization(Map<String, Object> request);

    /** Obtém o token de autorização para consultar a margem do trabalhador. */
    Map<String, Object> authorizePortabilityEmploymentQuery(Map<String, Object> request);

    /** Consulta margem, vínculo e contratos elegíveis para portabilidade. */
    Map<String, Object> getPortabilityEmploymentBalance(String productId, String taxpayerId);

    /** Cria o bundle que solicita a emissão da CCB de portabilidade. */
    Map<String, Object> createPortabilityBundle(Map<String, Object> request);

    Map<String, Object> getPortabilityBundle(String bundleId);

    /** Baixa o termo de autorização assinado pelo tomador. */
    byte[] downloadPortabilityAuthorization(String taxpayerId);

    Map<String, Object> createPerson(Map<String, Object> request);

    Map<String, Object> getPerson(String personId);

    Map<String, Object> updatePerson(String personId, Map<String, Object> request);

    Map<String, Object> createBusiness(Map<String, Object> request);

    Map<String, Object> getBusiness(String businessId);

    Map<String, Object> updateBusiness(String businessId, Map<String, Object> request);

    Map<String, Object> createBusinessRelation(String businessId, Map<String, Object> request);

    Map<String, Object> getQualification(String productId, String applicationId);

    Map<String, Object> qualifyApplication(String productId, String applicationId, Map<String, Object> request);

    Map<String, Object> removeGuarantee(String applicationId, String reason);

    Map<String, Object> createWebhook(Map<String, Object> request);

    Map<String, Object> listWebhooks();

    Map<String, Object> updateWebhook(String webhookId, Map<String, Object> request);

    void deleteWebhook(String webhookId);

    /** Envia uma oferta para uma solicitação do leilão interno Crédito do Trabalhador. */
    Map<String, Object> submitWorkersCreditOffer(String proposalId, Map<String, Object> offer);

    /** Consulta margem, saldo disponível e períodos do FGTS do trabalhador. */
    Map<String, Object> getFgtsBalance(String productId, String taxpayerId);

    /** Consulta os eventos de escrituração, repasse e alteração de vínculo. */
    Map<String, Object> listGuaranteeEvents(Map<String, Object> filters);

    /** Consulta o estado da garantia e o histórico de ações da averbação. */
    Map<String, Object> getGuaranteeStatus(String applicationId);

    /** Autenticação da API para o produto de servidores do Exército. */
    CelcoinCreditTokenResponse authenticateArmyConsigned();

    /** Consulta a margem consignável de um servidor do Exército. */
    Map<String, Object> getArmyConsignedMargin(String productId, String taxpayerId);

    /** Simula uma CCB do consignado de servidores do Exército. */
    Map<String, Object> simulateArmyConsignedCcb(String productId, Map<String, Object> request);

    /** Cria o tomador do consignado, com análise KYC da plataforma. */
    Map<String, Object> createArmyConsignedBorrower(Map<String, Object> request);

    /** Solicita a compra de CCBs de outros bancos com liberação de troco. */
    Map<String, Object> createArmyConsignedPurchaseBundle(Map<String, Object> request);

    /** Consulta o andamento de um bundle de compra com troco. */
    Map<String, Object> getArmyConsignedPurchaseBundle(String bundleId);

    /** Consulta o status atual da operação do consignado. */
    Map<String, Object> getArmyConsignedOperationStatus(String applicationId);
}
