CREATE TABLE mobile_kyc_document (
    id UUID PRIMARY KEY,
    proposal_id VARCHAR(160) NOT NULL,
    filename VARCHAR(512) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    content BYTEA NOT NULL
);
CREATE INDEX idx_mobile_kyc_document_proposal ON mobile_kyc_document(proposal_id, created_at DESC);
