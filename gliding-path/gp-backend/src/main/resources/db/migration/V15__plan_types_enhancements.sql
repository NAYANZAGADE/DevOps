-- Consolidated migration for plan types (replaces old V15–V18)
-- 1) Create features table
CREATE TABLE IF NOT EXISTS plan_type_feature (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_type_id UUID NOT NULL REFERENCES plan_type(id) ON DELETE CASCADE,
    label TEXT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- 2) Add copy fields to plan_type
ALTER TABLE plan_type
  ADD COLUMN IF NOT EXISTS headline VARCHAR(255),
  ADD COLUMN IF NOT EXISTS long_description TEXT;

-- 3) Ensure Solo 401(k) exists
INSERT INTO plan_type (
  tenant_id, created_by, updated_by, name, description, monthly_cost, per_participant_fee,
  employer_account_fee, employee_account_fee, employer_contribution, employee_contribution_limit,
  compliance_protection, tax_credit
)
SELECT 'system','system','system','Solo 401(k)',
       'Maximum employee/employer contributions with tax benefits for self-employed individuals',
       0,0,0,0,'Not applicable',23500,'N/A','N/A'
WHERE NOT EXISTS (SELECT 1 FROM plan_type WHERE name = 'Solo 401(k)');

-- 4) Seed features for each plan (idempotent by label)
-- Starter 401(k)
INSERT INTO plan_type_feature (plan_type_id, label, display_order, tenant_id, created_by, updated_by)
SELECT pt.id, f.label, f.display_order, 'system', 'system', 'system'
FROM (VALUES (1,'Auto-Enrollment'),(2,'Flexible eligibility criteria'),(3,'Expert selected fund list'),(4,'Standard & custom portfolios')) AS f(display_order,label)
JOIN plan_type pt ON pt.name = 'Starter 401(k)'
WHERE NOT EXISTS (
  SELECT 1 FROM plan_type_feature t WHERE t.plan_type_id = pt.id AND t.label = f.label
);

-- Safe Harbor 401(k)
INSERT INTO plan_type_feature (plan_type_id, label, display_order, tenant_id, created_by, updated_by)
SELECT pt.id, f.label, f.display_order, 'system', 'system', 'system'
FROM (VALUES (1,'Auto-Enrollment'),(2,'Flexible eligibility criteria'),(3,'Employer contribution criteria'),(4,'Expert selected fund list'),(5,'Standard & custom portfolios'),(6,'Non-discrimination testing exemption')) AS f(display_order,label)
JOIN plan_type pt ON pt.name = 'Safe Harbor 401(k)'
WHERE NOT EXISTS (
  SELECT 1 FROM plan_type_feature t WHERE t.plan_type_id = pt.id AND t.label = f.label
);

-- Traditional 401(k)
INSERT INTO plan_type_feature (plan_type_id, label, display_order, tenant_id, created_by, updated_by)
SELECT pt.id, f.label, f.display_order, 'system', 'system', 'system'
FROM (VALUES (1,'Auto-Enrollment'),(2,'Flexible eligibility criteria'),(3,'Employer contribution options'),(4,'Vesting options'),(5,'Expert selected fund list'),(6,'Standard & custom portfolios'),(7,'Non-discrimination testing required')) AS f(display_order,label)
JOIN plan_type pt ON pt.name = 'Traditional 401(k)'
WHERE NOT EXISTS (
  SELECT 1 FROM plan_type_feature t WHERE t.plan_type_id = pt.id AND t.label = f.label
);

-- Solo 401(k)
-- First remove any existing Solo features (in case prior seeds exist)
DELETE FROM plan_type_feature WHERE plan_type_id IN (SELECT id FROM plan_type WHERE name = 'Solo 401(k)');
INSERT INTO plan_type_feature (plan_type_id, label, display_order, tenant_id, created_by, updated_by)
SELECT pt.id, f.label, f.display_order, 'system', 'system', 'system'
FROM (VALUES (1,'Auto-Enrollment'),(2,'Standard & custom portfolios'),(3,'Employee & Employer contributions'),(4,'Record-keeping & IRS reporting'),(5,'Expert selected fund list')) AS f(display_order,label)
JOIN plan_type pt ON pt.name = 'Solo 401(k)';

-- 5) Seed marketing copy
UPDATE plan_type SET 
  headline = 'Starter is the simplest and most affordable',
  long_description = 'This plan option satisfies state mandates and helps companies provide their employees with a basic 401(k) plan without making employer contributions.'
WHERE name = 'Starter 401(k)';

UPDATE plan_type SET 
  headline = 'Safe Harbor maximizes contributions while minimizing compliance testing',
  long_description = 'This plan option allows participants to maximize their contributions and requires an employer contribution in exchange for exemption from most compliance testing.'
WHERE name = 'Safe Harbor 401(k)';

UPDATE plan_type SET 
  headline = 'Traditional is the most flexible but requires the most compliance testing',
  long_description = 'This plan option allows participants to maximize their contributions and provides employers with various options to contribute or not, including a vesting schedule. However, it requires more administrative effort due to the IRS compliance testing requirement.'
WHERE name = 'Traditional 401(k)';

UPDATE plan_type SET 
  headline = 'Self-employed Plan Option',
  long_description = 'This plan option allows self-employed individuals to maximize their contributions both as employee and employer.'
WHERE name = 'Solo 401(k)'; 