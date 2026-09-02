package com.brunopedraca.celcoin.bff.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mobile_request_audit")
public class MobileRequestAudit {
    @Id
    private UUID id;

    @Column(nullable = false, length = 16)
    private String method;

    @Column(nullable = false, length = 512)
    private String path;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(name = "correlation_id", nullable = false, length = 120)
    private String correlationId;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected MobileRequestAudit() {}

    private MobileRequestAudit(String method, String path, int statusCode, String correlationId, long durationMs) {
        this.id = UUID.randomUUID();
        this.method = method;
        this.path = path;
        this.statusCode = statusCode;
        this.correlationId = correlationId;
        this.durationMs = durationMs;
        this.createdAt = OffsetDateTime.now();
    }

    public static MobileRequestAudit of(
            String method, String path, int statusCode, String correlationId, long durationMs) {
        return new MobileRequestAudit(method, path, statusCode, correlationId, durationMs);
    }
}
