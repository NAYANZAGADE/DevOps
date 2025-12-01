-- Create finch connection mapping table
CREATE TABLE IF NOT EXISTS finch_connection_mapping (
    id SERIAL PRIMARY KEY,
    connection_id VARCHAR(255) NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL
);

-- Create webhook event log table
CREATE TABLE webhook_event_log (
    event_id VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP
); 