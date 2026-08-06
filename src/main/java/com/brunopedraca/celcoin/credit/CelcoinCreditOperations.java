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
}
