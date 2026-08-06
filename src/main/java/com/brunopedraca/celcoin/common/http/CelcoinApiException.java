package com.brunopedraca.celcoin.common.http;

import com.brunopedraca.celcoin.common.exception.CelcoinException;
import org.springframework.http.HttpStatusCode;

public class CelcoinApiException extends CelcoinException {
    public CelcoinApiException(String message, HttpStatusCode status, String correlationId, String remoteRequestId) {
        this(message, status, null, correlationId, remoteRequestId);
    }

    public CelcoinApiException(
            String message, HttpStatusCode status, String remoteCode, String correlationId, String remoteRequestId) {
        super(message, status, remoteCode, correlationId, remoteRequestId, null);
    }
}
