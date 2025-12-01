-- Create master_vesting_schedules table
CREATE TABLE IF NOT EXISTS master_vesting_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    schedule_type VARCHAR(50) NOT NULL,
    years_to_full_vest INTEGER,
    description TEXT,
    is_system_default BOOLEAN DEFAULT true,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create tenant_vesting_schedules table
CREATE TABLE IF NOT EXISTS tenant_vesting_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    schedule_type VARCHAR(50) NOT NULL,
    years_to_full_vest INTEGER,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create vesting_schedule_details table
CREATE TABLE IF NOT EXISTS vesting_schedule_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vesting_schedule_id UUID REFERENCES master_vesting_schedules(id),
    tenant_vesting_schedule_id UUID REFERENCES tenant_vesting_schedules(id),
    years_of_service INTEGER NOT NULL,
    vested_percentage DOUBLE PRECISION NOT NULL,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Insert Master Vesting Schedules
INSERT INTO master_vesting_schedules (id, name, schedule_type, years_to_full_vest, description, is_system_default, created_at, created_by, updated_at, updated_by) 
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'Standard 4-Year Graded', 'graded', 4, 'Standard 4-year graded vesting schedule with 25% vesting each year', true, NOW(), 'system', NOW(), 'system'),
    ('550e8400-e29b-41d4-a716-446655440002', '3-Year Cliff', 'cliff', 3, '3-year cliff vesting schedule - 100% vesting after 3 years', true, NOW(), 'system', NOW(), 'system'),
    ('550e8400-e29b-41d4-a716-446655440003', 'Immediate Vesting', 'immediate', 0, 'Immediate 100% vesting upon grant', true, NOW(), 'system', NOW(), 'system'),
    ('550e8400-e29b-41d4-a716-446655440004', '6-Year Graded', 'graded', 6, '6-year graded vesting schedule with 16.67% vesting each year', true, NOW(), 'system', NOW(), 'system'),
    ('550e8400-e29b-41d4-a716-446655440005', '2-Year Cliff', 'cliff', 2, '2-year cliff vesting schedule - 100% vesting after 2 years', true, NOW(), 'system', NOW(), 'system')
ON CONFLICT (id) DO NOTHING;

-- Insert Tenant Vesting Schedules
INSERT INTO tenant_vesting_schedules (id, name, schedule_type, years_to_full_vest, tenant_id, created_at, created_by, updated_at, updated_by) 
VALUES 
    ('550e8400-e29b-41d4-a716-446655440003', 'Company Custom 4-Year', 'graded', 4, 'tenant-123', NOW(), 'system', NOW(), 'system'),
    ('550e8400-e29b-41d4-a716-446655440004', 'Company Custom 3-Year Cliff', 'cliff', 3, 'tenant-123', NOW(), 'system', NOW(), 'system'),
    ('550e8400-e29b-41d4-a716-446655440005', 'Company Custom Immediate', 'immediate', 0, 'tenant-123', NOW(), 'system', NOW(), 'system')
ON CONFLICT (id) DO NOTHING;

-- Insert Vesting Schedule Details for Master Schedules
INSERT INTO vesting_schedule_details (id, vesting_schedule_id, years_of_service, vested_percentage, created_at, created_by, updated_at, updated_by) 
VALUES 
    -- Standard 4-Year Graded
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440001', 1, 25.00, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440001', 2, 50.00, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440001', 3, 75.00, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440001', 4, 100.00, NOW(), 'system', NOW(), 'system'),
    
    -- 3-Year Cliff
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440002', 1, 0.00, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440002', 2, 0.00, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440002', 3, 100.00, NOW(), 'system', NOW(), 'system'),
    
    -- Immediate Vesting
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440003', 0, 100.00, NOW(), 'system', NOW(), 'system'),
    
    -- 6-Year Graded
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440004', 1, 16.67, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440004', 2, 33.33, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440004', 3, 50.00, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440004', 4, 66.67, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440004', 5, 83.33, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440004', 6, 100.00, NOW(), 'system', NOW(), 'system'),
    
    -- 2-Year Cliff
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440005', 1, 0.00, NOW(), 'system', NOW(), 'system'),
    (gen_random_uuid(), '550e8400-e29b-41d4-a716-446655440005', 2, 100.00, NOW(), 'system', NOW(), 'system')
ON CONFLICT (id) DO NOTHING;

-- Check master vesting schedules
SELECT * FROM master_vesting_schedules;

-- Check tenant vesting schedules
SELECT * FROM tenant_vesting_schedules;

-- Check vesting schedule details
SELECT * FROM vesting_schedule_details; 