package com.brunopedraca.celcoin.common.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "celcoin_idempotency_record")
public class CelcoinIdempotencyRecord {
    public static final String STATUS_STARTED = "STARTED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    @Id
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private String operation;

    @Column(name = "request_hash", nullable = false)
    private String requestHash;

    @Column(name = "response_hash")
    private String responseHash;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "last_error")
    private String lastError;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    protected CelcoinIdempotencyRecord() {}

    public CelcoinIdempotencyRecord(String idempotencyKey, String operation, String requestHash) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.operation = operation;
        this.requestHash = requestHash;
        this.status = STATUS_STARTED;
        this.createdAt = OffsetDateTime.now();
    }

    public void complete(String responseHash, String responseBody) {
        this.responseHash = responseHash;
        this.responseBody = responseBody;
        this.status = STATUS_COMPLETED;
        this.completedAt = OffsetDateTime.now();
    }

    public void fail(String error) {
        this.lastError = error;
        this.status = STATUS_FAILED;
        this.completedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getOperation() {
        return operation;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public String getResponseHash() {
        return responseHash;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getLastError() {
        return lastError;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }
}
