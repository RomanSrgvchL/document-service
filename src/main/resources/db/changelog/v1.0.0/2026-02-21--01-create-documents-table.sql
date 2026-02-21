-- liquibase formatted sql

-- changeset roman:1
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    author VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    document_number VARCHAR(36) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- changeset roman:2
CREATE INDEX idx_documents_created_at ON documents(created_at);
CREATE INDEX idx_documents_search ON documents(author, status, created_at DESC);