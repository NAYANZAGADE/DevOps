package constants;

public final class EligibilityConstants {
    
    // Private constructor to prevent instantiation
    private EligibilityConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // Eligibility Status Constants
    public static final String STATUS_ELIGIBLE = "ELIGIBLE";
    public static final String STATUS_NOT_ELIGIBLE = "NOT_ELIGIBLE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    
    // Eligibility Check Intervals (in months)
    public static final int ELIGIBLE_CHECK_MONTHS = 6;        // Check eligible employees every 6 months
    public static final int SERVICE_CHECK_MONTHS = 3;         // Check service-based eligibility every 3 months
    public static final int AGE_CHECK_MONTHS = 12;            // Check age-based eligibility every 12 months
    public static final int DEFAULT_CHECK_MONTHS = 1;         // Default monthly check for other cases
    
    // Age Check Intervals (in years)
    public static final int AGE_CHECK_YEARS = 1;              // Check age-based eligibility annually
    
    // Service Check Intervals (in months)
    public static final int SERVICE_CHECK_MONTHS_QUARTERLY = 3;  // Quarterly service checks
    public static final int SERVICE_CHECK_MONTHS_MONTHLY = 1;    // Monthly service checks
    
    // Business Rule Constants
    public static final String REASON_AGE = "age";
    public static final String REASON_SERVICE = "service";
    public static final String REASON_EMPLOYMENT_STATUS = "employment_status";
    public static final String REASON_PLAN_CONFIGURATION = "plan_configuration";
    
    // Eligibility Notes Templates
    public static final String NOTES_PROCESSED_BY_DROOLS = "Processed by Drools rule engine on %s";
    public static final String NOTES_MANUAL_OVERRIDE = "Manually overridden on %s";
    public static final String NOTES_SYSTEM_UPDATE = "System update on %s";
    
    // Error Messages
    public static final String ERROR_NO_PLAN_CONFIG = "No plan configuration found for tenant: %s";
    public static final String ERROR_NO_ELIGIBILITY_CONFIG = "No eligibility configuration found for tenant: %s";
    public static final String ERROR_EVALUATION_FAILED = "Failed to evaluate eligibility: %s";
    public static final String ERROR_CONVERSION_FAILED = "Failed to convert PlanParticipant to DTO: %s";
    public static final String ERROR_FIND_NEWLY_ELIGIBLE_FAILED = "Failed to find newly eligible employees: %s";
    public static final String ERROR_PROCESS_ELIGIBILITY_CHECK_FAILED = "Failed to process eligibility check: %s";
    
    // Log Messages
    public static final String LOG_NO_TENANT_PLAN = "No tenant plan found for tenant: {}";
    public static final String LOG_NO_ELIGIBILITY_CONFIG = "No plan eligibility configuration found for tenant: {}";
    public static final String LOG_EMPLOYEE_ELIGIBLE = "Employee {} is eligible for benefits";
    public static final String LOG_EMPLOYEE_NOT_ELIGIBLE = "Employee {} is not eligible: {}";
} 