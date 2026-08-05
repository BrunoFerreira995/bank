ALTER TABLE celcoin_idempotency_record
    ADD COLUMN response_body TEXT,
    ADD COLUMN last_error TEXT;

CREATE INDEX idx_celcoin_idempotency_operation ON celcoin_idempotency_record(operation);
CREATE INDEX idx_celcoin_idempotency_status ON celcoin_idempotency_record(status);
