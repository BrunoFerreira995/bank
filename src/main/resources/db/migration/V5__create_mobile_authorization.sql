CREATE TABLE mobile_user_role (
    user_id UUID NOT NULL REFERENCES mobile_user(id),
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT chk_mobile_user_role CHECK (role IN ('CUSTOMER', 'SUPPORT', 'OPERATIONS', 'ADMIN'))
);

CREATE TABLE mobile_account_grant (
    user_id UUID NOT NULL REFERENCES mobile_user(id),
    account_id VARCHAR(160) NOT NULL,
    permission VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, account_id, permission),
    CONSTRAINT chk_mobile_account_grant_permission CHECK (permission IN ('READ', 'WRITE', 'RISK'))
);

ALTER TABLE mobile_session ADD COLUMN mfa_verified_at TIMESTAMPTZ;
