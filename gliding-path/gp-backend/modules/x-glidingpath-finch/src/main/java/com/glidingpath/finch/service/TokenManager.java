package com.glidingpath.finch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.glidingpath.finch.dto.TokenAccessResult;

public interface TokenManager {
    String getValidAccessToken(String tenantId) throws Exception;
    void storeToken(String tenantId, JsonNode tokenResponse) throws Exception;
    String getConnectionId(String tenantId);
    String getCustomerId(String tenantId);
    boolean isReauthRequired(String tenantId);
    void markReauthRequired(String tenantId);
    void clearReauthRequired(String tenantId);
    boolean shouldGenerateNewReauthUrl(String tenantId);
    
    /**
     * Gets a valid access token with automatic reauthentication handling.
     * This method encapsulates all the logic for handling expired tokens,
     * reauthentication flow, and error responses.
     * 
     * @param tenantId The tenant identifier
     * @return TokenAccessResult containing either the token or an error response
     */
    TokenAccessResult getTokenWithReauthHandling(String tenantId) throws Exception;
} 