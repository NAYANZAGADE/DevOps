-- Create table for company details
CREATE TABLE IF NOT EXISTS finch_company_details (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) UNIQUE,
    legal_name VARCHAR(255),
    ein VARCHAR(255),
    entity_type VARCHAR(255),
    entity_subtype VARCHAR(255),
    primary_email VARCHAR(255),
    primary_phone_number VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Departments (as a simple array for now, or you can normalize if needed)
CREATE TABLE IF NOT EXISTS finch_company_department (
    id SERIAL PRIMARY KEY,
    company_id UUID REFERENCES finch_company_details(id),
    name VARCHAR(255),
    parent VARCHAR(255)
);

-- Locations
CREATE TABLE IF NOT EXISTS finch_company_location (
    id SERIAL PRIMARY KEY,
    company_id UUID REFERENCES finch_company_details(id),
    city VARCHAR(255),
    country VARCHAR(255),
    line1 VARCHAR(255),
    line2 VARCHAR(255),
    postal_code VARCHAR(255),
    state VARCHAR(255),
    name VARCHAR(255)
);

-- Accounts
CREATE TABLE IF NOT EXISTS finch_company_account (
    id SERIAL PRIMARY KEY,
    company_id UUID REFERENCES finch_company_details(id),
    institution_name VARCHAR(255),
    account_name VARCHAR(255),
    account_type VARCHAR(255),
    account_number VARCHAR(255),
    routing_number VARCHAR(255)
);

-- Employee details
CREATE TABLE IF NOT EXISTS finch_employee_details (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255),
    company_id UUID REFERENCES finch_company_details(id),
    individual_id VARCHAR(255) UNIQUE,
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    preferred_name VARCHAR(255),
    gender VARCHAR(255),
    ethnicity VARCHAR(255),
    dob DATE,
    is_active BOOLEAN,
    title VARCHAR(255),
    employment_status VARCHAR(255),
    start_date DATE,
    end_date DATE,
    latest_rehire_date DATE,
    class_code VARCHAR(255),
    -- Embedded fields
    manager_id VARCHAR(255),
    department_name VARCHAR(255),
    employment_type VARCHAR(255),
    employment_subtype VARCHAR(255),
    -- Location embedded fields
    location_line1 VARCHAR(255),
    location_line2 VARCHAR(255),
    location_city VARCHAR(255),
    location_state VARCHAR(255),
    location_postal_code VARCHAR(255),
    location_country VARCHAR(255),
    -- Residence embedded fields
    residence_line1 VARCHAR(255),
    residence_line2 VARCHAR(255),
    residence_city VARCHAR(255),
    residence_state VARCHAR(255),
    residence_postal_code VARCHAR(255),
    residence_country VARCHAR(255),
    -- Income embedded fields
    income_unit VARCHAR(255),
    income_amount BIGINT,
    income_currency VARCHAR(255),
    income_effective_date VARCHAR(255),
    -- Eligibility tracking fields
    is_eligible_for_401k BOOLEAN DEFAULT false,
    eligibility_date DATE,
    last_eligibility_check DATE,
    eligibility_reason VARCHAR(500),
    eligibility_status VARCHAR(50),
    next_eligibility_check_date DATE,
    eligibility_notes TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Employee emails
CREATE TABLE IF NOT EXISTS finch_employee_email (
    id SERIAL PRIMARY KEY,
    employee_id UUID REFERENCES finch_employee_details(id),
    data VARCHAR(255),
    type VARCHAR(50)
);

-- Employee phone numbers
CREATE TABLE IF NOT EXISTS finch_employee_phone_number (
    id SERIAL PRIMARY KEY,
    employee_id UUID REFERENCES finch_employee_details(id),
    data VARCHAR(255),
    type VARCHAR(50)
);

-- Employee income history
CREATE TABLE IF NOT EXISTS finch_employee_income_history (
    id SERIAL PRIMARY KEY,
    employee_id UUID REFERENCES finch_employee_details(id),
    unit VARCHAR(50),
    amount BIGINT,
    currency VARCHAR(10),
    effective_date DATE
);

-- Employee custom fields
CREATE TABLE IF NOT EXISTS finch_employee_custom_field (
    id SERIAL PRIMARY KEY,
    employee_id UUID REFERENCES finch_employee_details(id),
    name VARCHAR(255),
    value VARCHAR(255)
);

-- Create finch_benefits table
CREATE TABLE IF NOT EXISTS finch_benefits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    benefit_id VARCHAR(255) NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    benefit_type VARCHAR(100),
    description TEXT,
    frequency VARCHAR(100),
    company_contribution_type VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system'
);

-- Create finch_benefit_contribution_tiers table for storing contribution tiers
CREATE TABLE IF NOT EXISTS finch_benefit_contribution_tiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    benefit_id UUID NOT NULL,
    threshold INTEGER,
    match_amount INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (benefit_id) REFERENCES finch_benefits(id) ON DELETE CASCADE
);

-- Add indexes for better performance
CREATE INDEX idx_finch_employee_tenant_id ON finch_employee_details(tenant_id);
CREATE INDEX idx_finch_employee_company_id ON finch_employee_details(company_id);
CREATE INDEX idx_finch_company_tenant_id ON finch_company_details(tenant_id);
-- Create indexes for eligibility fields
CREATE INDEX IF NOT EXISTS idx_finch_employee_eligibility_status ON finch_employee_details(eligibility_status);
CREATE INDEX IF NOT EXISTS idx_finch_employee_eligible_401k ON finch_employee_details(is_eligible_for_401k);
CREATE INDEX IF NOT EXISTS idx_finch_employee_last_check ON finch_employee_details(last_eligibility_check);
-- Create indexes for Finch benefits
CREATE INDEX IF NOT EXISTS idx_finch_benefits_tenant_id ON finch_benefits(tenant_id);
CREATE INDEX IF NOT EXISTS idx_finch_benefits_benefit_id ON finch_benefits(benefit_id);
CREATE INDEX IF NOT EXISTS idx_finch_benefits_tenant_benefit ON finch_benefits(tenant_id, benefit_id); 