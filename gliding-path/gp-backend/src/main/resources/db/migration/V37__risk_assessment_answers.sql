-- Risk Assessment Answers Table
-- Stores user responses to retirement planning questionnaire
-- Questions are stored as constants in backend, only question_id and answer stored here

CREATE TABLE risk_assessment_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    question_id INTEGER NOT NULL,
    answer TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    -- Ensure one answer per question per user per tenant
    CONSTRAINT uk_risk_assessment_tenant_user_question 
        UNIQUE(tenant_id, user_id, question_id)
);

-- Index for efficient queries by tenant and user
CREATE INDEX idx_risk_assessment_tenant_user 
    ON risk_assessment_answers(tenant_id, user_id);

-- Index for efficient queries by question
CREATE INDEX idx_risk_assessment_question 
    ON risk_assessment_answers(question_id);

-- Comments for documentation
COMMENT ON TABLE risk_assessment_answers IS 'Stores user responses to retirement planning risk assessment questionnaire';
COMMENT ON COLUMN risk_assessment_answers.question_id IS 'References question ID from RiskAssessmentConstants';
COMMENT ON COLUMN risk_assessment_answers.answer IS 'User selected answer text for the question';
