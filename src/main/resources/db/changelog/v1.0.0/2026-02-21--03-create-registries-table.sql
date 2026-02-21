-- liquibase formatted sql

-- changeset roman:5
CREATE TABLE registries (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL UNIQUE REFERENCES documents(id) ON DELETE CASCADE,
    approver VARCHAR(30) NOT NULL,
    approved_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- changeset roman:6
CREATE INDEX idx_registries_document_id ON registries(document_id);