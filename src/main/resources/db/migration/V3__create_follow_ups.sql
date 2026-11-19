CREATE TABLE follow_ups (
    id              BIGSERIAL PRIMARY KEY,
    application_id  BIGINT NOT NULL REFERENCES applications (id) ON DELETE CASCADE,
    due_date        DATE NOT NULL,
    note            TEXT,
    done            BOOLEAN NOT NULL DEFAULT FALSE,
    overdue_flagged BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_follow_ups_due ON follow_ups (due_date, done);