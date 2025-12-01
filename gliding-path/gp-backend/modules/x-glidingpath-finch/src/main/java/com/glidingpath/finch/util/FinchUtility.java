package com.glidingpath.finch.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glidingpath.finch.config.FinchConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class FinchUtility {
    
    // Private constructor to prevent instantiation
    private FinchUtility() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Builds HTTP headers for Finch API requests
     * @return configured HttpHeaders with JSON content type
     */
    public static HttpHeaders buildHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    /**
     * Builds request payload for token exchange
     * @param finchConfig Finch configuration containing client credentials
     * @param code authorization code received from Finch redirect
     * @return Map containing token exchange request payload
     */
    public static Map<String, String> buildTokenExchangePayload(FinchConfig finchConfig, String code) {
        Map<String, String> body = new HashMap<>();
        body.put("client_id", finchConfig.getClientId());
        body.put("client_secret", finchConfig.getClientSecret());
        body.put("code", code);
        body.put("redirect_uri", finchConfig.getRedirectUri());
        return body;
    }
    
    /**
     * Builds request payload for token refresh
     * @param finchConfig Finch configuration containing client credentials
     * @param refreshToken encrypted refresh token
     * @return Map containing token refresh request payload
     */
    public static Map<String, String> buildTokenRefreshPayload(FinchConfig finchConfig, String refreshToken) {
        Map<String, String> body = new HashMap<>();
        body.put("client_id", finchConfig.getClientId());
        body.put("client_secret", finchConfig.getClientSecret());
        body.put("refresh_token", refreshToken);
        body.put("grant_type", "refresh_token");
        return body;
    }
    
    /**
     * Parses JSON response with specific error handling
     * @param objectMapper Jackson ObjectMapper for JSON parsing
     * @param responseBody response body as string
     * @param operation operation name for error context
     * @return parsed JsonNode
     * @throws IOException if JSON parsing fails
     */
    public static JsonNode parseJsonResponse(ObjectMapper objectMapper, String responseBody, String operation) throws IOException {
            return objectMapper.readTree(responseBody);
    }
    
    /**
     * Creates HTTP entity for Finch API requests
     * @param headers HTTP headers
     * @param body request payload
     * @return configured HttpEntity
     */
    public static HttpEntity<Map<String, String>> createHttpEntity(HttpHeaders headers, Map<String, String> body) {
        return new HttpEntity<>(body, headers);
    }
    
    /**
     * Builds complete HTTP entity for token exchange
     * @param finchConfig Finch configuration
     * @param code authorization code
     * @return configured HttpEntity for token exchange
     */
    public static HttpEntity<Map<String, String>> buildTokenExchangeRequest(FinchConfig finchConfig, String code) {
        HttpHeaders headers = buildHttpHeaders();
        Map<String, String> body = buildTokenExchangePayload(finchConfig, code);
        return createHttpEntity(headers, body);
    }
    
    /**
     * Builds complete HTTP entity for token refresh
     * @param finchConfig Finch configuration
     * @param refreshToken refresh token
     * @return configured HttpEntity for token refresh
     */
    public static HttpEntity<Map<String, String>> buildTokenRefreshRequest(FinchConfig finchConfig, String refreshToken) {
        HttpHeaders headers = buildHttpHeaders();
        Map<String, String> body = buildTokenRefreshPayload(finchConfig, refreshToken);
        return createHttpEntity(headers, body);
    }
    
    /**
     * Builds request payload for reauthentication
     * @param finchConfig Finch configuration containing client credentials
     * @param connectionId Finch connection ID
     * @param customerId Finch customer ID
     * @return Map containing reauthentication request payload
     */
    public static Map<String, Object> buildReauthPayload(FinchConfig finchConfig, String connectionId, String customerId) {
        Map<String, Object> body = new HashMap<>();
        body.put("connection_id", connectionId);
        body.put("customer_id", customerId);
        body.put("redirect_uri", finchConfig.getRedirectUri());
        body.put("sandbox", true);
        return body;
    }
    
    /**
     * Builds complete HTTP entity for reauthentication
     * @param finchConfig Finch configuration
     * @param connectionId Finch connection ID
     * @param customerId Finch customer ID
     * @return configured HttpEntity for reauthentication
     */
    public static HttpEntity<Map<String, Object>> buildReauthRequest(FinchConfig finchConfig, String connectionId, String customerId) {
        HttpHeaders headers = buildHttpHeaders();
        headers.set("Finch-API-Version", finchConfig.getApiVersion());
        Map<String, Object> body = buildReauthPayload(finchConfig, connectionId, customerId);
        return new HttpEntity<>(body, headers);
    }
} 