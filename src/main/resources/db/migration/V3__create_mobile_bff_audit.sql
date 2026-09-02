CREATE TABLE mobile_request_audit (
    id UUID PRIMARY KEY,
    method VARCHAR(16) NOT NULL,
    path VARCHAR(512) NOT NULL,
    status_code INTEGER NOT NULL,
    correlation_id VARCHAR(120) NOT NULL,
    duration_ms BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_mobile_request_audit_correlation_id ON mobile_request_audit(correlation_id);
