package com.brunopedraca.celcoin.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "celcoin_api_audit")
public class CelcoinApiAudit {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String path;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "request_summary")
    private String requestSummary;

    @Column(name = "response_summary")
    private String responseSummary;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected CelcoinApiAudit() {}
}
