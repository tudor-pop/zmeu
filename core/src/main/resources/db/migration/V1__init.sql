CREATE TABLE resources
(
    id         UUID        NOT NULL,
    properties JSONB,
    type       VARCHAR(255),
    existing   BOOLEAN,
    name       VARCHAR(255),
    created_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_on TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_resources PRIMARY KEY (id)
);

CREATE INDEX idx_name ON resources (name);
