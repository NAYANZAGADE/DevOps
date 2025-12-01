-- Create company onboarding state table
CREATE TABLE IF NOT EXISTS company_onboarding_state (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    current_state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_company_onboarding_tenant ON company_onboarding_state(tenant_id);
CREATE INDEX IF NOT EXISTS idx_company_onboarding_state ON company_onboarding_state(current_state);