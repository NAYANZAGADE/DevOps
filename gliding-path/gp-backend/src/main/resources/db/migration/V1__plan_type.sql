CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS plan_type (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    monthly_cost INT NOT NULL,
    per_participant_fee INT NOT NULL,
    employer_account_fee DECIMAL(10,2) DEFAULT 0,
    employee_account_fee DECIMAL(10,2) DEFAULT 0,
    employer_contribution VARCHAR(255),
    employee_contribution_limit INT,
    compliance_protection VARCHAR(255),
    tax_credit VARCHAR(255)
    );

INSERT INTO plan_type (
    tenant_id,
    created_by,
    updated_by,
    name,
    description,
    monthly_cost,
    per_participant_fee,
    employer_account_fee,
    employee_account_fee,
    employer_contribution,
    employee_contribution_limit,
    compliance_protection,
    tax_credit
) VALUES
      ('system', 'system', 'system', 'Starter 401(k)', 'Affordable plan with no employer match', 39, 4, 0, 0, 'Not eligible', 6000, 'Exempt from compliance tests', 'Get back up to $16,500'),
      ('system', 'system', 'system', 'Safe Harbor 401(k)', 'Includes match and compliance ease', 89, 8, 0, 0.15, 'Requires minimum of 3%', 23500, 'Satisfies most compliance tests', 'Get back up to $16,500 + employer contribution credit'),
      ('system', 'system', 'system', 'Traditional 401(k)', 'Optional match, more admin work', 89, 8, 0, 0, 'Optional', 23500, 'Has compliance monitoring', 'Get back up to $16,500 + employer contribution credit');
