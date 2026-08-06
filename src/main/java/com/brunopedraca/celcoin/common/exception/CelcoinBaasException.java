package com.brunopedraca.celcoin.common.exception;

import com.brunopedraca.celcoin.common.error.CelcoinBaasErrors;
import com.brunopedraca.celcoin.common.http.CelcoinApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;

/** Exceção funcional que preserva o código e a recomendação retornados pelo BaaS. */
public class CelcoinBaasException extends CelcoinApiException {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final CelcoinBaasErrors.Error error;

    public CelcoinBaasException(
            String message, HttpStatusCode status, String code, String correlationId,
            String remoteRequestId, CelcoinBaasErrors.Error error) {
        super(message, status, code, correlationId, remoteRequestId);
        this.error = error;
    }

    public CelcoinBaasErrors.Error error() {
        return error;
    }

    public static CelcoinApiException from(
            String payload, HttpStatusCode status, String correlationId, String remoteRequestId) {
        try {
            JsonNode root = MAPPER.readTree(payload);
            JsonNode errorNode = root.path("error");
            String code = text(errorNode, "errorCode");
            if (code == null) code = text(root, "errorCode");
            if (code == null) return new CelcoinApiException(payload, status, correlationId, remoteRequestId);
            CelcoinBaasErrors.Error descriptor = CelcoinBaasErrors.find(code);
            String message = text(errorNode, "message");
            if (message == null) message = text(root, "message");
            return new CelcoinBaasException(message == null ? descriptor.message() : message, status, code,
                    correlationId, remoteRequestId, descriptor);
        } catch (Exception ignored) {
            return new CelcoinApiException(payload, status, correlationId, remoteRequestId);
        }
    }

    private static String text(JsonNode node, String name) {
        JsonNode value = node == null ? null : node.get(name);
        return value != null && value.isTextual() && !value.asText().isBlank() ? value.asText() : null;
    }
}
