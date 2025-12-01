package com.glidingpath.finch.constants;

import java.time.format.DateTimeFormatter;

public final class FinchConstants {
    
    private FinchConstants() {
        // Prevent instantiation
    }
    
    // Date formatting
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Batch processing
    public static final int BATCH_SIZE = 100;
    public static final int CHUNK_SIZE = 50;
    public static final int BATCH_RETRY_LIMIT = 3;
    public static final int SKIP_LIMIT = 1000;
    
    // Error messages
    public static final String ERROR_INDIVIDUAL_NOT_FOUND = "Failed to fetch individual data for employee: ";
    public static final String ERROR_EMPLOYMENT_NOT_FOUND = "Failed to fetch employment data for employee: ";
    public static final String ERROR_DATE_PARSE = "Failed to parse date: ";
    public static final String BATCH_INIT_ERROR = "Failed to initialize batch components: ";
    
    // Message templates
    public static final String SUCCESS_MESSAGE_TEMPLATE = "Successfully synced employees. Processed: %d, New: %d, Existing: %d, Failed: %d";
    public static final String ERROR_MESSAGE_TEMPLATE = "Spring Batch job failed for tenantId: %s. Status: %s";
    
    // Job parameter keys
    public static final String JOB_PARAM_TENANT_ID = "tenantId";
    public static final String JOB_PARAM_TIMESTAMP = "timestamp";
} 