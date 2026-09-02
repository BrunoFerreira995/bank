CREATE TABLE mobile_consent (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES mobile_user(id),
    type VARCHAR(100) NOT NULL,
    version VARCHAR(100) NOT NULL,
    accepted_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_mobile_consent_user ON mobile_consent(user_id);
