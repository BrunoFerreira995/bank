package com.brunopedraca.celcoin.common.exception;

import java.time.Instant;
import org.springframework.http.HttpStatusCode;

public class CelcoinException extends RuntimeException {
    private final HttpStatusCode status;
    private final String remoteCode;
    private final String correlationId;
    private final String remoteRequestId;
    private final Instant occurredAt;

    public CelcoinException(String message) {
        this(message, null, null, null, null, null);
    }

    public CelcoinException(
            String message,
            HttpStatusCode status,
            String remoteCode,
            String correlationId,
            String remoteRequestId,
            Throwable cause) {
        super(message, cause);
        this.status = status;
        this.remoteCode = remoteCode;
        this.correlationId = correlationId;
        this.remoteRequestId = remoteRequestId;
        this.occurredAt = Instant.now();
    }

    public HttpStatusCode status() {
        return status;
    }

    public String remoteCode() {
        return remoteCode;
    }

    public String correlationId() {
        return correlationId;
    }

    public String remoteRequestId() {
        return remoteRequestId;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
