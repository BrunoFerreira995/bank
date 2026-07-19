package com.brunopedraca.celcoin.common.http;

import com.brunopedraca.celcoin.common.exception.CelcoinException;
import org.springframework.http.HttpStatusCode;

public class CelcoinApiException extends CelcoinException {
    public CelcoinApiException(String message, HttpStatusCode status, String correlationId, String remoteRequestId) {
        super(message, status, null, correlationId, remoteRequestId, null);
    }
}
