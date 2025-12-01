-- Create plan_sponsor_details table for storing company/plan sponsor information 
CREATE TABLE IF NOT EXISTS plan_sponsor_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    
    -- Basic Company Information
    legal_name VARCHAR(255) NOT NULL,
    ein VARCHAR(255),
    entity_type VARCHAR(255),
    
    -- Business Address (matches Embedded Class fields)
    street VARCHAR(255),
    apt VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    postal_code VARCHAR(255),
    phone_number VARCHAR(255),
    mailing_different BOOLEAN DEFAULT false,
    
    -- Payroll Information (matches Embedded Class fields)
    payroll_provider VARCHAR(255),
    schedule VARCHAR(255),
    number_of_days INTEGER,
    
    -- Employee Information
    estimated_employee_count INTEGER,
    union_employees BOOLEAN DEFAULT false,
    leased_employees BOOLEAN DEFAULT false,
    
    -- Retirement Plan Information
    existing_retirement_plan BOOLEAN DEFAULT false,
    related_entities BOOLEAN DEFAULT false,
    
    -- NEW FIELDS FROM SCREENSHOTS
    -- Employment Status (Sign Up 1)
    employment_status VARCHAR(100), -- 'independent_contractor', 'business_owner_no_w2', 'business_owner_with_w2', 'representing_company'
    
    -- Business Size (Sign Up 2)
    business_size VARCHAR(50), -- '0-5', '6-50', '51-99', '100-500', '500+'
    
    -- Retirement Plan Priority (Sign Up 3)
    retirement_plan_priority VARCHAR(100), -- 'maximizing_savings', 'enabling_employees', 'retaining_talent', 'compliance', 'unsure'
    
    -- Existing 401k Plan (Sign Up 5)
    has_existing_401k BOOLEAN DEFAULT false,
    
    -- Multiple Businesses (Sign Up 7)
    has_multiple_businesses BOOLEAN DEFAULT false,
    
    -- Audit Fields (from BaseEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_plan_sponsor_tenant_id ON plan_sponsor_details(tenant_id);
CREATE INDEX IF NOT EXISTS idx_plan_sponsor_legal_name ON plan_sponsor_details(legal_name);
CREATE INDEX IF NOT EXISTS idx_plan_sponsor_ein ON plan_sponsor_details(ein);
CREATE INDEX IF NOT EXISTS idx_plan_sponsor_employment_status ON plan_sponsor_details(employment_status);
CREATE INDEX IF NOT EXISTS idx_plan_sponsor_business_size ON plan_sponsor_details(business_size);

-- Add comments for documentation
COMMENT ON TABLE plan_sponsor_details IS 'Stores comprehensive information about plan sponsors and companies including employment status, business size, and retirement plan preferences';
COMMENT ON COLUMN plan_sponsor_details.employment_status IS 'Employment status of the plan sponsor (independent contractor, business owner, etc.)';
COMMENT ON COLUMN plan_sponsor_details.business_size IS 'Size of the business in terms of employee count ranges';
COMMENT ON COLUMN plan_sponsor_details.retirement_plan_priority IS 'Primary reason for seeking a 401(k) retirement plan';
COMMENT ON COLUMN plan_sponsor_details.has_existing_401k IS 'Whether the company currently has an existing 401(k) plan';
COMMENT ON COLUMN plan_sponsor_details.has_multiple_businesses IS 'Whether the business owner operates multiple businesses';
