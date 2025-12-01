package com.glidingpath.common.util;

public enum ErrorCode {
    USER_NOT_FOUND("USR_404"),
    NOT_FOUND("GEN_404"),
    INVALID_INPUT("GEN_400"),
    UNAUTHORIZED("AUTH_401"),
    FORBIDDEN("AUTH_403"),
    INTERNAL_ERROR("GEN_500"),
    KEYCLOAK_ERROR("KC_500"),
    
    // Finch-specific error codes
    FINCH_API_ERROR("FINCH_API_500"),
    FINCH_AUTH_ERROR("FINCH_AUTH_401"),
    FINCH_RATE_LIMIT("FINCH_RATE_429"),
    FINCH_DATA_NOT_FOUND("FINCH_DATA_404"),
    FINCH_BATCH_ERROR("FINCH_BATCH_500"),
    FINCH_INITIALIZATION_ERROR("FINCH_INIT_500"),
    FINCH_MAPPING_ERROR("FINCH_MAP_500"),
    FINCH_CACHE_ERROR("FINCH_CACHE_500");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() { 
        return code; 
    }
} 