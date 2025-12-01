-- Add email column to plan_sponsor_details table
ALTER TABLE plan_sponsor_details ADD COLUMN email VARCHAR(255);

-- Add mailing address columns to plan_sponsor_details table
ALTER TABLE plan_sponsor_details ADD COLUMN mailing_street VARCHAR(255);
ALTER TABLE plan_sponsor_details ADD COLUMN mailing_apt VARCHAR(255);
ALTER TABLE plan_sponsor_details ADD COLUMN mailing_city VARCHAR(255);
ALTER TABLE plan_sponsor_details ADD COLUMN mailing_state VARCHAR(255);
ALTER TABLE plan_sponsor_details ADD COLUMN mailing_postal_code VARCHAR(255);
ALTER TABLE plan_sponsor_details ADD COLUMN mailing_phone_number VARCHAR(255);

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_plan_sponsor_email ON plan_sponsor_details(email);
CREATE INDEX IF NOT EXISTS idx_plan_sponsor_mailing_state ON plan_sponsor_details(mailing_state);

-- Add comments for documentation
COMMENT ON COLUMN plan_sponsor_details.email IS 'Primary email address for the company/plan sponsor';
COMMENT ON COLUMN plan_sponsor_details.mailing_street IS 'Mailing address street (when different from business address)';
COMMENT ON COLUMN plan_sponsor_details.mailing_apt IS 'Mailing address apartment/suite (when different from business address)';
COMMENT ON COLUMN plan_sponsor_details.mailing_city IS 'Mailing address city (when different from business address)';
COMMENT ON COLUMN plan_sponsor_details.mailing_state IS 'Mailing address state (when different from business address)';
COMMENT ON COLUMN plan_sponsor_details.mailing_postal_code IS 'Mailing address postal code (when different from business address)';
COMMENT ON COLUMN plan_sponsor_details.mailing_phone_number IS 'Mailing address phone number (when different from business address)';
