-- liquibase formatted sql

-- changeset roman:3
CREATE TABLE document_history (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    initiator VARCHAR(30) NOT NULL,
    comment VARCHAR(500),
    action VARCHAR(20) NOT NULL CHECK (action IN ('SUBMIT', 'APPROVE')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- changeset roman:4
CREATE INDEX idx_document_history_document_id ON document_history(document_id);