CREATE TABLE applications (
    id              BIGSERIAL PRIMARY KEY,
    company         VARCHAR(255) NOT NULL,
    role            VARCHAR(255) NOT NULL,
    job_url         VARCHAR(2048),
    source          VARCHAR(50) NOT NULL,
    salary_min      INTEGER,
    salary_max      INTEGER,
    location        VARCHAR(255),
    remote          BOOLEAN NOT NULL DEFAULT FALSE,
    notes           TEXT,
    applied_date    DATE NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'APPLIED',
    last_activity_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_applications_status ON applications (status);
CREATE INDEX idx_applications_company ON applications (company);
CREATE INDEX idx_applications_source ON applications (source);
CREATE INDEX idx_applications_applied_date ON applications (applied_date);