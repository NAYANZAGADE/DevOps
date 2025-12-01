-- Add Finch reauthentication fields to tokens table
ALTER TABLE tokens ADD COLUMN connection_id VARCHAR(255);
ALTER TABLE tokens ADD COLUMN customer_id VARCHAR(255);
ALTER TABLE tokens ADD COLUMN last_reauth_at TIMESTAMP;
ALTER TABLE tokens ADD COLUMN reauth_required BOOLEAN DEFAULT FALSE;

-- Add indexes for better performance
CREATE INDEX idx_tokens_connection_id ON tokens(connection_id);
CREATE INDEX idx_tokens_customer_id ON tokens(customer_id);
