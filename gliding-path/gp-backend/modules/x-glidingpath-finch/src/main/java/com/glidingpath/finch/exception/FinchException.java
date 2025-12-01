package com.glidingpath.finch.exception;

import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;

public class FinchException extends AppException {
    
    public FinchException(ErrorCode errorCode) {
        super(errorCode, errorCode.getCode());
    }
    
    public FinchException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public FinchException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
    
    // Convenience constructors for common Finch errors
    public static FinchException apiError(String message) {
        return new FinchException(ErrorCode.FINCH_API_ERROR, message);
    }
    
    public static FinchException apiError(String message, Throwable cause) {
        return new FinchException(ErrorCode.FINCH_API_ERROR, message, cause);
    }
    
    public static FinchException authError(String message) {
        return new FinchException(ErrorCode.FINCH_AUTH_ERROR, message);
    }
    
    public static FinchException dataNotFound(String message) {
        return new FinchException(ErrorCode.FINCH_DATA_NOT_FOUND, message);
    }
    
    public static FinchException batchError(String message) {
        return new FinchException(ErrorCode.FINCH_BATCH_ERROR, message);
    }
    
    public static FinchException initializationError(String message) {
        return new FinchException(ErrorCode.FINCH_INITIALIZATION_ERROR, message);
    }
    
    public static FinchException mappingError(String message) {
        return new FinchException(ErrorCode.FINCH_MAPPING_ERROR, message);
    }
    
    public static FinchException cacheError(String message) {
        return new FinchException(ErrorCode.FINCH_CACHE_ERROR, message);
    }
} 