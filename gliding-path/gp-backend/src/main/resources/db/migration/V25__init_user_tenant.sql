-- Create tenant table
CREATE TABLE tenant (
    id UUID PRIMARY KEY,
    org_id VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create user table
CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    preferred_username VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    sub VARCHAR(255),
    tenant_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);

CREATE INDEX idx_user_preferred_username_tenant_id ON "user" (preferred_username, tenant_id);