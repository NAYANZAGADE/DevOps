-- Create plan_eligibility table
CREATE TABLE IF NOT EXISTS plan_eligibility (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    minimum_entry_age INTEGER,
    time_employed_months INTEGER,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create employee_contribution_config table
CREATE TABLE IF NOT EXISTS employee_contribution_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    default_contribution_rate DOUBLE PRECISION,
    enrollment_start_rate DOUBLE PRECISION,
    enrollment_max_rate DOUBLE PRECISION,
    enrollment_annual_increase DOUBLE PRECISION,
    enrollment_max_contribution_rate DOUBLE PRECISION,
    has_employee_contribution BOOLEAN DEFAULT true,
    is_auto_enrollment BOOLEAN DEFAULT false,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create employer_contribution_rule table
CREATE TABLE IF NOT EXISTS employer_contribution_rule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_type VARCHAR(50),
    match_percentage DOUBLE PRECISION,
    basic_match_first_percent DOUBLE PRECISION,
    basic_match_first_rate DOUBLE PRECISION,
    basic_match_second_percent DOUBLE PRECISION,
    basic_match_second_rate DOUBLE PRECISION,
    match_limit_percent DOUBLE PRECISION,
    flexible_match_percent DOUBLE PRECISION,
    non_elective_percent DOUBLE PRECISION,
    vesting_schedule_id UUID,
    tenant_vesting_schedule_id UUID,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create profit_sharing_config table
CREATE TABLE IF NOT EXISTS profit_sharing_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    is_enabled BOOLEAN DEFAULT false,
    default_contribution VARCHAR(50),
    flat_dollar_amount DOUBLE PRECISION,
    pro_rata_percentage DOUBLE PRECISION,
    comparability_formula VARCHAR(255),
    vesting_schedule_id UUID,
    tenant_vesting_schedule_id UUID,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create tenant_plan table
CREATE TABLE IF NOT EXISTS tenant_plan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_type_id UUID NOT NULL,
    plan_year INTEGER NOT NULL,
    effective_date DATE,
    eligibility_id UUID,
    employee_contribution_config_id UUID,
    employer_contribution_rule_id UUID,
    profit_sharing_config_id UUID,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    FOREIGN KEY (plan_type_id) REFERENCES plan_type(id),
    FOREIGN KEY (eligibility_id) REFERENCES plan_eligibility(id),
    FOREIGN KEY (employee_contribution_config_id) REFERENCES employee_contribution_config(id),
    FOREIGN KEY (employer_contribution_rule_id) REFERENCES employer_contribution_rule(id),
    FOREIGN KEY (profit_sharing_config_id) REFERENCES profit_sharing_config(id)
);

-- Create plan_start_date table
CREATE TABLE IF NOT EXISTS plan_start_date (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_date DATE NOT NULL,
    onboarding_tasks_due DATE,
    employee_invites_sent DATE,
    paycheck_with_first_contribution DATE,
    tenant_plan_id UUID,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    FOREIGN KEY (tenant_plan_id) REFERENCES tenant_plan(id)
); 