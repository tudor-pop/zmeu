CREATE TABLE resources
(
    id         UUID NOT NULL,
    properties JSONB,
    type       VARCHAR(255),
    existing   BOOLEAN,
    name       VARCHAR(255),
    CONSTRAINT pk_resources PRIMARY KEY (id)
);

CREATE INDEX idx_name ON resources (name);
