CREATE TABLE celcoin_webhook_event (
    id UUID PRIMARY KEY,
    external_event_id VARCHAR(160) NOT NULL UNIQUE,
    event_type VARCHAR(120) NOT NULL,
    payload_json TEXT NOT NULL,
    headers_json TEXT NOT NULL,
    received_at TIMESTAMPTZ NOT NULL,
    processed_at TIMESTAMPTZ,
    processing_status VARCHAR(32) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT
);

CREATE INDEX idx_celcoin_webhook_event_status ON celcoin_webhook_event(processing_status);

CREATE TABLE celcoin_idempotency_record (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(160) NOT NULL UNIQUE,
    operation VARCHAR(160) NOT NULL,
    request_hash VARCHAR(128) NOT NULL,
    response_hash VARCHAR(128),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ
);

CREATE TABLE celcoin_api_audit (
    id UUID PRIMARY KEY,
    operation VARCHAR(160) NOT NULL,
    method VARCHAR(16) NOT NULL,
    path VARCHAR(512) NOT NULL,
    status_code INTEGER,
    correlation_id VARCHAR(120),
    idempotency_key VARCHAR(160),
    request_summary TEXT,
    response_summary TEXT,
    created_at TIMESTAMPTZ NOT NULL
);
