-- Spring Batch metadata tables
CREATE TABLE IF NOT EXISTS batch_job_instance (
    job_instance_id BIGINT PRIMARY KEY,
    version BIGINT,
    job_name VARCHAR(100) NOT NULL,
    job_key VARCHAR(32) NOT NULL,
    UNIQUE(job_name, job_key)
);

CREATE TABLE IF NOT EXISTS batch_job_execution (
    job_execution_id BIGINT PRIMARY KEY,
    version BIGINT,
    job_instance_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(10),
    exit_code VARCHAR(2500),
    exit_message VARCHAR(2500),
    last_updated TIMESTAMP,
    job_configuration_location VARCHAR(250),
    FOREIGN KEY (job_instance_id) REFERENCES batch_job_instance(job_instance_id)
);

CREATE TABLE IF NOT EXISTS batch_job_execution_params (
    job_execution_id BIGINT NOT NULL,
    parameter_name VARCHAR(100) NOT NULL,
    parameter_type VARCHAR(100) NOT NULL,
    parameter_value VARCHAR(2500),
    identifying CHAR(1) NOT NULL,
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
);

CREATE TABLE IF NOT EXISTS batch_job_execution_context (
    job_execution_id BIGINT NOT NULL,
    short_context VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    PRIMARY KEY (job_execution_id),
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
);

CREATE TABLE IF NOT EXISTS batch_step_execution (
    step_execution_id BIGINT PRIMARY KEY,
    version BIGINT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    job_execution_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(10),
    commit_count BIGINT,
    read_count BIGINT,
    filter_count BIGINT,
    write_count BIGINT,
    read_skip_count BIGINT,
    write_skip_count BIGINT,
    process_skip_count BIGINT,
    rollback_count BIGINT,
    exit_code VARCHAR(2500),
    exit_message VARCHAR(2500),
    last_updated TIMESTAMP,
    FOREIGN KEY (job_execution_id) REFERENCES batch_job_execution(job_execution_id)
);

CREATE TABLE IF NOT EXISTS batch_step_execution_context (
    step_execution_id BIGINT NOT NULL,
    short_context VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    PRIMARY KEY (step_execution_id),
    FOREIGN KEY (step_execution_id) REFERENCES batch_step_execution(step_execution_id)
);

-- Sequences for auto-incrementing IDs (using Spring Batch expected names)
CREATE SEQUENCE IF NOT EXISTS batch_job_seq;
CREATE SEQUENCE IF NOT EXISTS batch_job_execution_seq;
CREATE SEQUENCE IF NOT EXISTS batch_step_execution_seq;

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_batch_job_execution_job_instance_id ON batch_job_execution(job_instance_id);
CREATE INDEX IF NOT EXISTS idx_batch_step_execution_job_execution_id ON batch_step_execution(job_execution_id); 