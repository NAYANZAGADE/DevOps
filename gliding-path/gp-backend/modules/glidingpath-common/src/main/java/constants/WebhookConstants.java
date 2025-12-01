package constants;

public final class WebhookConstants {
    
    // Private constructor to prevent instantiation
    private WebhookConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // Event Type Constants
    public static final String EVENT_INDIVIDUAL = "individual";
    public static final String EVENT_EMPLOYMENT = "employment";
    public static final String EVENT_COMPANY = "company";
    public static final String EVENT_BENEFIT_JOB = "job";
    
    // Action Constants
    public static final String ACTION_CREATED = "created";
    public static final String ACTION_UPDATED = "updated";
    public static final String ACTION_DELETED = "deleted";
    public static final String ACTION_JOB_COMPLETED = "completed";
    
    // Sync Type Constants
    public static final String SYNC_TYPE_INDIVIDUAL = "INDIVIDUAL";
    public static final String SYNC_TYPE_EMPLOYMENT = "EMPLOYMENT";
    public static final String SYNC_TYPE_COMPANY = "COMPANY";
    public static final String SYNC_TYPE_BENEFIT = "BENEFIT";
    public static final String SYNC_TYPE_UNKNOWN = "UNKNOWN";
    
    // Action Type Constants
    public static final String ACTION_TYPE_CREATED = "CREATED";
    public static final String ACTION_TYPE_UPDATED = "UPDATED";
    public static final String ACTION_TYPE_DELETED = "DELETED";
    public static final String ACTION_TYPE_JOB_COMPLETED = "JOB_COMPLETED";
    public static final String ACTION_TYPE_UNKNOWN = "UNKNOWN";
    
    // Benefit Job Types
    public static final String BENEFIT_JOB_CREATE = "benefit_create";
    public static final String BENEFIT_JOB_ENROLL = "benefit_enroll";
    public static final String BENEFIT_JOB_UPDATE = "benefit_update";
    public static final String BENEFIT_JOB_DELETE = "benefit_delete";
    
    // Webhook Data Fields
    public static final String FIELD_INDIVIDUAL_ID = "individual_id";
    
    // Log Messages
    public static final String LOG_UNSUPPORTED_EVENT = "Unsupported webhook event type: {}";
    public static final String LOG_PROCESSING_EVENT = "Processing webhook event: eventType={}, connectionId={}, dataSize={}";
    public static final String LOG_EVENT_SUCCESS = "Webhook processed successfully: eventType={}, connectionId={}";
    public static final String LOG_UNHANDLED_EVENT = "Unhandled webhook event type: {}";
    public static final String LOG_MISSING_INDIVIDUAL_ID = "Missing individual_id in webhook data for event: {}";
    public static final String LOG_JOB_COMPLETION_DATA = "Job completion data: {}";
    
    // Error Messages
    public static final String ERROR_PROCESSING_WEBHOOK = "Error processing webhook event: {} for connectionId: {}";
    public static final String ERROR_UNEXPECTED_WEBHOOK = "Unexpected error in handleWebhook: {}";
} 