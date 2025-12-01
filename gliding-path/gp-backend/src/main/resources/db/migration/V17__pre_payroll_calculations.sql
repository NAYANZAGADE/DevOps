-- Migration: V17__pre_payroll_calculations.sql
-- Description: Create pre-payroll calculations table

-- Create pre-payroll calculations table with proper foreign key references
CREATE TABLE pre_payroll_calculations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    calculation_id VARCHAR(255) UNIQUE NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    employee_id UUID NOT NULL,
    payroll_period_start DATE NOT NULL,
    payroll_period_end DATE NOT NULL,
    calculation_date TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    reprocessed_count INTEGER DEFAULT 0,
    last_reprocessed_at TIMESTAMP,
    
    -- Calculation amounts
    base_salary DECIMAL(15,2),
    eligible_compensation DECIMAL(15,2),
    employee_contribution_amount DECIMAL(15,2),
    employee_contribution_percentage DECIMAL(5,2),
    employer_match_amount DECIMAL(15,2),
    employer_match_percentage DECIMAL(5,2),
    profit_sharing_amount DECIMAL(15,2),
    profit_sharing_percentage DECIMAL(5,2),
    total_contribution_amount DECIMAL(15,2),
    total_contribution_percentage DECIMAL(5,2),
    
    -- Configuration references
    plan_id UUID,
    employer_contribution_rule_id UUID,
    employee_contribution_config_id UUID,
    profit_sharing_config_id UUID,
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    -- Foreign key constraints
    CONSTRAINT fk_pre_payroll_calculations_employee
        FOREIGN KEY (employee_id) REFERENCES finch_employee_details(id)
        ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_pre_payroll_calculations_tenant_id ON pre_payroll_calculations(tenant_id);
CREATE INDEX idx_pre_payroll_calculations_employee_id ON pre_payroll_calculations(employee_id);
CREATE INDEX idx_pre_payroll_calculations_status ON pre_payroll_calculations(status);
CREATE INDEX idx_pre_payroll_calculations_period ON pre_payroll_calculations(payroll_period_start, payroll_period_end);
CREATE INDEX idx_pre_payroll_calculations_calculation_id ON pre_payroll_calculations(calculation_id);

-- Add comments for documentation
COMMENT ON TABLE pre_payroll_calculations IS 'Stores pre-payroll calculation results for employees';
COMMENT ON COLUMN pre_payroll_calculations.calculation_id IS 'Unique identifier for the calculation (tenant_employee_start_end)';
COMMENT ON COLUMN pre_payroll_calculations.employee_id IS 'Foreign key reference to finch_employee_details table';
COMMENT ON COLUMN pre_payroll_calculations.status IS 'Status of calculation: IN_PROGRESS, SUCCESS, FAILED, REPROCESSED';
COMMENT ON COLUMN pre_payroll_calculations.reprocessed_count IS 'Number of times this calculation has been reprocessed';