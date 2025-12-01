package constants;

public final class BatchConstants {
    
    private BatchConstants() {
        // Prevent instantiation
    }
    
    // ========================================
    // ELIGIBILITY BATCH CONFIGURATION
    // ========================================
    public static final int ELIGIBILITY_CHUNK_SIZE = 10;  // Reduced from 50 to prevent memory issues
    public static final int ELIGIBILITY_PAGE_SIZE = 100;  // Reduced from 1000 to prevent memory issues
    public static final int ELIGIBILITY_RETRY_LIMIT = 2;  // Reduced from 3 to prevent infinite loops
    public static final int ELIGIBILITY_SKIP_LIMIT = 5;   // Reduced from 50 to fail fast on errors
    
    // ========================================
    // CALCULATION BATCH CONFIGURATION
    // ========================================
    public static final int CALCULATION_CHUNK_SIZE = 5;   // Reduced from 25 to prevent memory issues
    public static final int CALCULATION_RETRY_LIMIT = 2;  // Reduced from 3 to prevent infinite loops
    public static final int CALCULATION_SKIP_LIMIT = 3;   // Reduced from 25 to fail fast on errors
    
    // ========================================
    // FINCH DEDUCTION BATCH CONFIGURATION
    // ========================================
    public static final int DEDUCTION_CHUNK_SIZE = 5;     // Reduced from 25 to prevent memory issues
    public static final int DEDUCTION_RETRY_LIMIT = 2;    // Reduced from 3 to prevent infinite loops
    public static final int DEDUCTION_SKIP_LIMIT = 3;     // Reduced from 10 to fail fast on errors
    
    // ========================================
    // GENERAL BATCH CONFIGURATION
    // ========================================
    public static final int DEFAULT_CHUNK_SIZE = 100;
    public static final int DEFAULT_RETRY_LIMIT = 3;
    public static final int DEFAULT_SKIP_LIMIT = 100;
    
    // ========================================
    // JOB PARAMETER KEYS
    // ========================================
    public static final String JOB_PARAM_TENANT_ID = "tenantId";
    public static final String JOB_PARAM_TIMESTAMP = "timestamp";
    public static final String JOB_PARAM_PAYROLL_PERIOD_START = "payrollPeriodStart";
    public static final String JOB_PARAM_PAYROLL_PERIOD_END = "payrollPeriodEnd";
    
    // ========================================
    // EXECUTION CONTEXT KEYS
    // ========================================
    public static final String CONTEXT_ELIGIBILITY_RESULTS = "eligibilityResults";
    public static final String CONTEXT_CALCULATION_RESULTS = "calculationResults";
    public static final String CONTEXT_DEDUCTION_RESULTS = "deductionResults";
    public static final String CONTEXT_TOTAL_PROCESSED = "totalProcessed";
    public static final String CONTEXT_SUCCESS_COUNT = "successCount";
    public static final String CONTEXT_FAILURE_COUNT = "failureCount";
    public static final String CONTEXT_SKIP_COUNT = "skipCount";
}
