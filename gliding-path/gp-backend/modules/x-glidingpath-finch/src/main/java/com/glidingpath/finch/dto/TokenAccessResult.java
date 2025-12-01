package com.glidingpath.finch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result DTO for token access operations.
 * Used to encapsulate both success (token) and error (errorResponse) states.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenAccessResult {
    private String token;
    private FinchReauthErrorResponse errorResponse;
    private boolean isError;
    
    /**
     * Creates a success result with a token
     */
    public static TokenAccessResult success(String token) {
        return TokenAccessResult.builder()
            .token(token)
            .isError(false)
            .build();
    }
    
    /**
     * Creates an error result with an error response
     */
    public static TokenAccessResult error(FinchReauthErrorResponse errorResponse) {
        return TokenAccessResult.builder()
            .errorResponse(errorResponse)
            .isError(true)
            .build();
    }
}

