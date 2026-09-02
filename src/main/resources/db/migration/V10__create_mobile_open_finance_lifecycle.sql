CREATE TABLE mobile_open_finance_flow (id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES mobile_user(id), type VARCHAR(64) NOT NULL, institution_id VARCHAR(160), consent_id VARCHAR(160), status VARCHAR(32) NOT NULL, created_at TIMESTAMPTZ NOT NULL);
CREATE TABLE mobile_open_finance_payment (id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES mobile_user(id), type VARCHAR(64) NOT NULL, consent_id VARCHAR(160), amount NUMERIC(19,2), scheduled_for DATE, status VARCHAR(32) NOT NULL, created_at TIMESTAMPTZ NOT NULL);
CREATE INDEX idx_mobile_of_flow_user ON mobile_open_finance_flow(user_id, created_at DESC);
CREATE INDEX idx_mobile_of_payment_user ON mobile_open_finance_payment(user_id, created_at DESC);
