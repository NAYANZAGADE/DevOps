-- Sys Config Table for JSON-based configurations
-- This table stores configuration objects that are served to frontend on application startup

CREATE TABLE IF NOT EXISTS sys_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value JSONB NOT NULL,
    config_type VARCHAR(50) NOT NULL, -- 'WEB_CONFIG', 'APP_CONFIG', 'BUSINESS_CONFIG', 'Common_Config'
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    tenant_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system'
);

-- Insert default configuration for plan start date
INSERT INTO sys_config (config_key, config_value, config_type, description, is_active, tenant_id, created_at, updated_at, created_by, updated_by) VALUES
('common_config', 
'{
  "companyOnboardingConfig": {
    "planStartDateConfig": {
      "onBoardingTasksDueIn": 60,
      "employeeInvitesSentIn": 40,
      "paycheckWithFirstContributionIn": 45
    }
  }
}'::jsonb, 
'CommonConfig', 
'Common configuration for company onboarding including plan start date settings',
true,
NULL,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
'system',
'system');

 