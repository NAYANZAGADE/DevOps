-- Add test company data
INSERT INTO finch_company_details (id, tenant_id, legal_name, ein, entity_type, entity_subtype, primary_email, primary_phone_number, created_at, updated_at, created_by, updated_by)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001',
    'tenant1',
    'Test Company Inc',
    '12-3456789',
    'CORPORATION',
    'C_CORP',
    'admin@testcompany.com',
    '555-123-4567',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
);

-- Add test employees
INSERT INTO finch_employee_details (
    id, tenant_id, company_id, individual_id, first_name, last_name, 
    dob, is_active, title, employment_status, start_date, 
    employment_type, department_name, is_eligible_for_401k, 
    eligibility_date, last_eligibility_check, eligibility_reason, 
    eligibility_status, created_at, updated_at, created_by, updated_by
) VALUES 
-- Employee 1: John Doe - Full time, hired 6 months ago, should be eligible
(
    '550e8400-e29b-41d4-a716-446655440002',
    'tenant1',
    '550e8400-e29b-41d4-a716-446655440001',
    'EMP001',
    'John',
    'Doe',
    '1990-01-15',
    true,
    'Software Engineer',
    'ACTIVE',
    '2024-02-15', -- 6 months ago
    'FULL_TIME',
    'Engineering',
    false, -- Will be determined by eligibility rules
    null,
    null,
    null,
    'PENDING',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
),

-- Employee 2: Jane Smith - Full time, hired 1 year ago, should be eligible
(
    '550e8400-e29b-41d4-a716-446655440003',
    'tenant1',
    '550e8400-e29b-41d4-a716-446655440001',
    'EMP002',
    'Jane',
    'Smith',
    '1988-05-20',
    true,
    'Product Manager',
    'ACTIVE',
    '2023-08-15', -- 1 year ago
    'FULL_TIME',
    'Product',
    false, -- Will be determined by eligibility rules
    null,
    null,
    null,
    'PENDING',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
),

-- Employee 3: Bob Johnson - Part time, hired 3 months ago, should NOT be eligible
(
    '550e8400-e29b-41d4-a716-446655440004',
    'tenant1',
    '550e8400-e29b-41d4-a716-446655440001',
    'EMP003',
    'Bob',
    'Johnson',
    '1992-12-10',
    true,
    'Marketing Assistant',
    'ACTIVE',
    '2024-05-15', -- 3 months ago
    'PART_TIME',
    'Marketing',
    false, -- Will be determined by eligibility rules
    null,
    null,
    null,
    'PENDING',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
),

-- Employee 4: Alice Brown - Full time, hired 2 years ago, should be eligible
(
    '550e8400-e29b-41d4-a716-446655440005',
    'tenant1',
    '550e8400-e29b-41d4-a716-446655440001',
    'EMP004',
    'Alice',
    'Brown',
    '1985-03-25',
    true,
    'Senior Developer',
    'ACTIVE',
    '2022-08-01', -- 2 years ago
    'FULL_TIME',
    'Engineering',
    false, -- Will be determined by eligibility rules
    null,
    null,
    null,
    'PENDING',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
),

-- Employee 5: Charlie Wilson - Contractor, should NOT be eligible
(
    '550e8400-e29b-41d4-a716-446655440006',
    'tenant1',
    '550e8400-e29b-41d4-a716-446655440001',
    'EMP005',
    'Charlie',
    'Wilson',
    '1991-07-08',
    true,
    'UI Designer',
    'ACTIVE',
    '2024-01-10', -- 7 months ago
    'CONTRACTOR',
    'Design',
    false, -- Will be determined by eligibility rules
    null,
    null,
    null,
    'PENDING',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
);

-- Add some departments
INSERT INTO finch_company_department (company_id, name, parent) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Engineering', null),
('550e8400-e29b-41d4-a716-446655440001', 'Product', null),
('550e8400-e29b-41d4-a716-446655440001', 'Marketing', null),
('550e8400-e29b-41d4-a716-446655440001', 'Design', null);

-- Add company location
INSERT INTO finch_company_location (company_id, city, country, line1, line2, postal_code, state, name) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'San Francisco', 'USA', '123 Tech Street', 'Suite 100', '94105', 'CA', 'Headquarters');
