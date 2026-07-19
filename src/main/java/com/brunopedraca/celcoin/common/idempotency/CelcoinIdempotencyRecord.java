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
        this.status = "STARTED";
        this.createdAt = OffsetDateTime.now();
    }
}
