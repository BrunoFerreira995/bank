CREATE TABLE mobile_user (
    id UUID PRIMARY KEY,
    login VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(512) NOT NULL,
    mfa_secret VARCHAR(256),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE mobile_user_account (
    user_id UUID NOT NULL REFERENCES mobile_user(id),
    account_id VARCHAR(160) NOT NULL,
    PRIMARY KEY (user_id, account_id)
);

CREATE TABLE mobile_session (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES mobile_user(id),
    access_token_hash VARCHAR(64) NOT NULL UNIQUE,
    refresh_token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    refresh_expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_mobile_session_access_token ON mobile_session(access_token_hash);

CREATE TABLE mobile_mfa_challenge (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES mobile_user(id),
    expires_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL
);
