CREATE TABLE status_history (
    id              BIGSERIAL PRIMARY KEY,
    application_id  BIGINT NOT NULL REFERENCES applications (id) ON DELETE CASCADE,
    from_status     VARCHAR(50),
    to_status       VARCHAR(50) NOT NULL,
    changed_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    note            TEXT
);

CREATE INDEX idx_status_history_application ON status_history (application_id, changed_at);