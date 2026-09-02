CREATE TABLE mobile_ticket (id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES mobile_user(id), subject VARCHAR(512) NOT NULL, description VARCHAR(4000) NOT NULL, status VARCHAR(32) NOT NULL, updated_at TIMESTAMPTZ NOT NULL);
CREATE TABLE mobile_notification (id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES mobile_user(id), title VARCHAR(512) NOT NULL, body VARCHAR(4000) NOT NULL, category VARCHAR(64) NOT NULL, read_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL);
CREATE INDEX idx_mobile_ticket_user ON mobile_ticket(user_id, updated_at DESC);
CREATE INDEX idx_mobile_notification_user ON mobile_notification(user_id, created_at DESC);
