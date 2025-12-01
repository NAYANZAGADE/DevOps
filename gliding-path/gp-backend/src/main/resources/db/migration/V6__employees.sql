-- Create employees table for rule engine testing
CREATE TABLE IF NOT EXISTS employees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    date_of_birth DATE,
    hire_date DATE,
    rehire_date DATE,
    employment_status VARCHAR(50), -- ACTIVE, INACTIVE, TERMINATED
    employment_type VARCHAR(50), -- FULL_TIME, PART_TIME, CONTRACTOR
    hours_worked_last_year INTEGER,
    is_eligible_for_401k BOOLEAN DEFAULT false,
    eligibility_date DATE,
    last_eligibility_check DATE,
    eligibility_reason VARCHAR(500), -- Why eligible/not eligible
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create index on tenant_id for performance
CREATE INDEX IF NOT EXISTS idx_employees_tenant_id ON employees(tenant_id);

-- Create index on employee_id for performance
CREATE INDEX IF NOT EXISTS idx_employees_employee_id ON employees(employee_id); 