package com.glidingpath.finch.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.glidingpath.finch.entity.TokenEntity;
import com.glidingpath.finch.repository.TokenRepository;
import com.glidingpath.finch.service.FinchService;
import com.glidingpath.finch.service.TokenManager;
import com.glidingpath.finch.dto.FinchReauthErrorResponse;
import com.glidingpath.finch.dto.TokenAccessResult;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.CryptoUtil;
import com.glidingpath.common.util.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenManagerImpl implements TokenManager {

    private final FinchService finchService;
    private final TokenRepository tokenRepository;

    @Value("${encryption.secret.key}")
    private String secretKey;

    /**
     * Stores encrypted access and refresh tokens for a tenant
     * @param tenantId The identifier for the tenant (e.g. "CopmanyID")
     * @param tokenResponse JSON response from Finch containing tokens
     * @throws Exception 
     * @throws AppException if token response is invalid or encryption fails
     */
    @Override
    @Transactional
    public void storeToken(String tenantId, JsonNode tokenResponse) throws Exception{
        log.info("Storing tokens for tenant: {}", tenantId);
        
        // Validate token response
        validateTokenResponse(tokenResponse);
        
        // Extract and encrypt tokens
        String encryptedAccessToken = encryptToken(tokenResponse.path("access_token").asText());
        String encryptedRefreshToken = null;
        if (tokenResponse.has("refresh_token") && !tokenResponse.path("refresh_token").asText().isEmpty()) {
            encryptedRefreshToken = encryptToken(tokenResponse.path("refresh_token").asText());
        }
        int expiresIn = tokenResponse.path("expires_in").asInt(3600); // default to 1 hour
        
        // Extract connection details if available
        String connectionId = null;
        String customerId = null;
        if (tokenResponse.has("connection_id")) {
            connectionId = tokenResponse.path("connection_id").asText();
        }
        if (tokenResponse.has("customer_id")) {
            customerId = tokenResponse.path("customer_id").asText();
        }
        
        // Build and persist token entity
        TokenEntity entity = buildTokenEntity(tenantId, encryptedAccessToken, encryptedRefreshToken, expiresIn, connectionId, customerId);
        tokenRepository.save(entity);
        
        log.info("Successfully stored encrypted tokens for tenant: {} with expiration: {} seconds", 
            tenantId, expiresIn);
    }

    /**
     * Returns a valid (non-expired) access token for a tenant, refreshing if necessary
     * @param tenantId ID of the tenant whose token is requested
     * @return decrypted valid access token
     * @throws AppException if token is not found, expired, or refresh fails
     * @throws Exception if Finch API call fails
     */
    @Override
    @Transactional(readOnly = true)
    public String getValidAccessToken(String tenantId) throws Exception {
        log.debug("Retrieving valid access token for tenant: {}", tenantId);
        
        // Fetch token for the given tenant
        TokenEntity entity = findTokenByTenantId(tenantId);
        
        // Check if token is expired and refresh if necessary
        if (isTokenExpired(entity)) {
            log.info("Access token expired for tenant: {}. Attempting refresh", tenantId);
            entity = refreshExpiredToken(tenantId, entity);
        }
        
        // Return decrypted access token
        String decryptedToken = CryptoUtil.decrypt(entity.getAccessToken(), secretKey);
        log.debug("Successfully retrieved valid access token for tenant: {}", tenantId);
        return decryptedToken;
    }

    // Private helper methods for code organization and reusability
    
    /**
     * Validates the token response structure
     */
    private void validateTokenResponse(JsonNode tokenResponse) {
        if (tokenResponse == null || !tokenResponse.has("access_token")) {
            log.error("Invalid token response: missing access_token");
            throw new AppException(ErrorCode.INVALID_INPUT, "Missing access token in response");
        }
    }
    
    /**
     * Encrypts a token using the configured secret key
     * @throws Exception 
     */
    private String encryptToken(String token) throws Exception{
        return CryptoUtil.encrypt(token, secretKey);
    }
    
    /**
     * Builds a TokenEntity with the provided data
     */
    private TokenEntity buildTokenEntity(String tenantId, String encryptedAccessToken, 
                                       String encryptedRefreshToken, int expiresIn, String connectionId, String customerId) {
        TokenEntity entity = new TokenEntity();
        entity.setTenantId(tenantId);
        entity.setAccessToken(encryptedAccessToken);
        entity.setRefreshToken(encryptedRefreshToken); // Can be null if no refresh token
        entity.setExpiresAt(Instant.now().plusSeconds(expiresIn));
        entity.setConnectionId(connectionId);
        entity.setCustomerId(customerId);
        entity.setReauthRequired(false); // Clear reauth flag when storing new tokens
        return entity;
    }
    
    /**
     * Finds a token by tenant ID with proper exception handling
     */
    private TokenEntity findTokenByTenantId(String tenantId) {
        return tokenRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 
                    "Token not found for tenant: " + tenantId));
    }
    
    /**
     * Checks if a token is expired
     */
    private boolean isTokenExpired(TokenEntity entity) {
        return Instant.now().isAfter(entity.getExpiresAt());
    }
    
    /**
     * Refreshes an expired token by calling Finch API
     */
    private TokenEntity refreshExpiredToken(String tenantId, TokenEntity entity) throws Exception {
        // Check if we have a refresh token to use
        if (entity.getRefreshToken() == null) {
            log.error("Cannot refresh token for tenant {}: no refresh token available", tenantId);
            throw new AppException(ErrorCode.INVALID_INPUT, 
                "Token expired and no refresh token available. Re-authentication required.");
        }
        
        // Decrypt refresh token and call Finch API
        String decryptedRefreshToken = CryptoUtil.decrypt(entity.getRefreshToken(), secretKey);
        JsonNode newToken = finchService.refreshAccessToken(decryptedRefreshToken);
        
        // Store the new tokens
        storeToken(tenantId, newToken);
        
        // Refetch the updated entity
        return tokenRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_ERROR, 
                    "Failed to retrieve refreshed token for tenant: " + tenantId));
    }
    
    /**
     * Gets the connection ID for a tenant
     * @param tenantId tenant identifier
     * @return connection ID or null if not found
     */
    @Override
    @Transactional(readOnly = true)
    public String getConnectionId(String tenantId) {
        return tokenRepository.findByTenantId(tenantId)
                .map(TokenEntity::getConnectionId)
                .orElse(null);
    }
    
    /**
     * Gets the customer ID for a tenant
     * @param tenantId tenant identifier
     * @return customer ID or null if not found
     */
    @Override
    @Transactional(readOnly = true)
    public String getCustomerId(String tenantId) {
        return tokenRepository.findByTenantId(tenantId)
                .map(TokenEntity::getCustomerId)
                .orElse(null);
    }
    
    /**
     * Checks if reauthentication is required for a tenant
     * @param tenantId tenant identifier
     * @return true if reauthentication is required
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isReauthRequired(String tenantId) {
        return tokenRepository.findByTenantId(tenantId)
                .map(TokenEntity::getReauthRequired)
                .orElse(false);
    }
    
    /**
     * Marks reauthentication as required for a tenant
     * @param tenantId tenant identifier
     */
    @Override
    @Transactional
    public void markReauthRequired(String tenantId) {
        TokenEntity entity = findTokenByTenantId(tenantId);
        entity.setReauthRequired(true);
        entity.setLastReauthAt(Instant.now());
        tokenRepository.save(entity);
        log.info("Marked reauthentication as required for tenant: {}", tenantId);
    }
    
    /**
     * Clears the reauthentication required flag for a tenant
     * @param tenantId tenant identifier
     */
    @Override
    @Transactional
    public void clearReauthRequired(String tenantId) {
        TokenEntity entity = findTokenByTenantId(tenantId);
        entity.setReauthRequired(false);
        entity.setLastReauthAt(null); // Clear timestamp when clearing reauth
        tokenRepository.save(entity);
        log.info("Cleared reauthentication required flag for tenant: {}", tenantId);
    }
    
    /**
     * Determines if a new reauthentication URL should be generated
     * @param tenantId tenant identifier
     * @return true if new reauth URL should be generated
     */
    @Override
    @Transactional(readOnly = true)
    public boolean shouldGenerateNewReauthUrl(String tenantId) {
        TokenEntity entity = tokenRepository.findByTenantId(tenantId).orElse(null);
        if (entity == null) {
            return true; // No token found, need to initiate
        }
        
        // Never initiated reauthentication
        if (!entity.getReauthRequired() || entity.getLastReauthAt() == null) {
            return true;
        }
        
        // Check if reauth URL has expired (15 minutes)
        Instant expiryTime = entity.getLastReauthAt().plus(java.time.Duration.ofMinutes(15));
        boolean expired = Instant.now().isAfter(expiryTime);
        
        if (expired) {
            log.info("Reauthentication URL expired for tenant: {}, generating new one", tenantId);
        }
        
        return expired;
    }
    
    /**
     * Gets a valid access token with automatic reauthentication handling.
     * This method encapsulates all the logic for handling expired tokens,
     * reauthentication flow, and error responses.
     * 
     * @param tenantId The tenant identifier
     * @return TokenAccessResult containing either the token or an error response
     */
    @Override
    @Transactional
    public TokenAccessResult getTokenWithReauthHandling(String tenantId) throws Exception {
        try {
            String token = getValidAccessToken(tenantId);
            return TokenAccessResult.success(token);
        } catch (AppException e) {
            // Check if this is a reauthentication required error
            if (e.getMessage().contains("Re-authentication required")) {
                // Get connection details from database
                String connectionId = getConnectionId(tenantId);
                String customerId = getCustomerId(tenantId);
                
                if (connectionId != null && customerId != null) {
                    try {
                        // Check if we need to generate a new reauth URL
                        if (shouldGenerateNewReauthUrl(tenantId)) {
                            // Generate new reauth URL
                            JsonNode reauthResponse = finchService.initiateReauthentication(connectionId, customerId);
                            String reauthUrl = reauthResponse.path("connect_url").asText();
                            
                            // Mark reauthentication as required with current timestamp
                            markReauthRequired(tenantId);
                            
                            // Return enhanced error response with new reauth URL
                            FinchReauthErrorResponse errorResponse = FinchReauthErrorResponse.builder()
                                    .timestamp(java.time.Instant.now().toString())
                                    .errorCode("GEN_400")
                                    .path("/v1/api/finch/auth/token")
                                    .description("Token expired and no refresh token available. Re-authentication required.")
                                    .reauthUrl(reauthUrl)
                                    .connectionId(connectionId)
                                    .customerId(customerId)
                                    .build();
                            
                            return TokenAccessResult.error(errorResponse);
                        } else {
                            // Reauth URL is still valid, return appropriate message
                            FinchReauthErrorResponse errorResponse = FinchReauthErrorResponse.builder()
                                    .timestamp(java.time.Instant.now().toString())
                                    .errorCode("GEN_400")
                                    .path("/v1/api/finch/auth/token")
                                    .description("Reauthentication in progress. Please complete the reauthentication process.")
                                    .reauthUrl(null) // No new URL needed
                                    .connectionId(connectionId)
                                    .customerId(customerId)
                                    .build();
                            
                            return TokenAccessResult.error(errorResponse);
                        }
                    } catch (Exception reauthException) {
                        // If reauthentication API fails, return original error
                        log.error("Failed to initiate reauthentication for tenant: {}", tenantId, reauthException);
                    }
                }
            }
            
            // Return error for the original exception
            FinchReauthErrorResponse errorResponse = FinchReauthErrorResponse.builder()
                    .timestamp(java.time.Instant.now().toString())
                    .errorCode("GEN_400")
                    .path("/v1/api/finch/auth/token")
                    .description(e.getMessage())
                    .reauthUrl(null)
                    .connectionId(null)
                    .customerId(null)
                    .build();
            
            return TokenAccessResult.error(errorResponse);
        }
    }
} 