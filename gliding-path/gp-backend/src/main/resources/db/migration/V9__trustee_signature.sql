-- Create trustee_confirmation table
CREATE TABLE IF NOT EXISTS trustee_confirmation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    is_trustee BOOLEAN NOT NULL,
    is_agree BOOLEAN NOT NULL,
    is_authorize BOOLEAN NOT NULL,
    confirmation_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    trustee_title VARCHAR(255),
    trustee_legal_name VARCHAR(255),
    trustee_email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create plan_signature table
CREATE TABLE IF NOT EXISTS plan_signature (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    signature_text TEXT NOT NULL,
    signature_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_documents_read BOOLEAN NOT NULL,
    is_changes_understood BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
); 