-- 1. Create identity table
CREATE SEQUENCE IF NOT EXISTS identity_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE identity
(
    id   INTEGER UNIQUE NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_identity PRIMARY KEY (id)
);
CREATE INDEX idx_name ON identity (name);


-- 2. Create resource_type table (without FKs to resources yet)
CREATE SEQUENCE IF NOT EXISTS ResourceType_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE resource_type
(
    id   INTEGER      NOT NULL,
    kind VARCHAR(255) NOT NULL,
    CONSTRAINT pk_resourcetype PRIMARY KEY (id)

);

---
-- 1. Resource have a UUID id (because it’s externally referenced, globally unique, etc.)
-- 2. Other entities like Identity to use simple INT primary keys (which are compact and faster)
CREATE TABLE resources
(
    id               UUID       NOT NULL,
    created_on       TIMESTAMP WITHOUT TIME ZONE,
    updated_on       TIMESTAMP WITHOUT TIME ZONE,
    properties       JSONB,
    existing         BOOLEAN,
    resource_type_id INT UNIQUE NOT NULL,
    identity_id      INT UNIQUE NOT NULL,
    CONSTRAINT pk_resources PRIMARY KEY (id),
    CONSTRAINT FK_RESOURCE_TYPE_ON_ID FOREIGN KEY (resource_type_id) REFERENCES resource_type (id),
    CONSTRAINT FK_IDENTITY_ON_ID FOREIGN KEY (identity_id) REFERENCES identity (id)
);
