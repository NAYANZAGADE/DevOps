package com.glidingpath.finch.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glidingpath.finch.config.FinchConfig;
import com.glidingpath.finch.service.FinchService;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import com.glidingpath.finch.util.FinchUtility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinchServiceImpl implements FinchService {

    private final FinchConfig finchConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Exchanges authorization code for access and refresh tokens using Finch API
     * @param code authorization code received from Finch redirect
     * @return token response as JsonNode
     * @throws IOException 
     * @throws AppException if token exchange fails
     */
    @Override
    public JsonNode exchangeCodeForTokens(String code) throws IOException {
        log.info("Exchanging authorization code for tokens");

         HttpEntity<Map<String, String>> request = FinchUtility.buildTokenExchangeRequest(finchConfig, code);

         ResponseEntity<String> response = restTemplate.postForEntity(
                 finchConfig.getTokenUrl(), request, String.class);

         if (!response.getStatusCode().is2xxSuccessful()) {
             log.error("Token exchange failed with status: {}", response.getStatusCode().value());

             throw new AppException(ErrorCode.INVALID_INPUT,
                     "Token exchange failed. Status: " + response.getStatusCode().value());
         }

         log.info("Token exchange successful");
         return FinchUtility.parseJsonResponse(objectMapper, response.getBody(), "token exchange");

     }

    /**
     * Refreshes access token using the given refresh token
     * @param refreshToken encrypted refresh token
     * @return new token response as JsonNode
     * @throws IOException 
     * @throws AppException if token refresh fails
     */
    @Override
    public JsonNode refreshAccessToken(String refreshToken) throws IOException {
       log.info("Refreshing access token");

        HttpEntity<Map<String, String>> request = FinchUtility.buildTokenRefreshRequest(finchConfig, refreshToken);

        ResponseEntity<String> response = restTemplate.postForEntity(
                finchConfig.getTokenUrl(), request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Token refresh failed with status: {}", response.getStatusCode().value());

            throw new AppException(ErrorCode.INVALID_INPUT,
                    "Token refresh failed. Status: " + response.getStatusCode().value());
        }

        log.info("Token refresh successful");
        return FinchUtility.parseJsonResponse(objectMapper, response.getBody(), "token refresh");

    }

    /**
     * Initiates reauthentication process for a given connection
     * @param connectionId the connection ID to reauthenticate
     * @param customerId the customer ID associated with the connection
     * @return reauthentication response as JsonNode
     * @throws IOException 
     * @throws AppException if reauthentication initiation fails
     */
    @Override
    public JsonNode initiateReauthentication(String connectionId, String customerId) throws IOException {
        log.info("Initiating reauthentication for connection: {} and customer: {}", connectionId, customerId);

        HttpEntity<Map<String, Object>> request = FinchUtility.buildReauthRequest(finchConfig, connectionId, customerId);

        ResponseEntity<String> response = restTemplate.postForEntity(
                finchConfig.getReauthUrl(), request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Reauthentication initiation failed with status: {}", response.getStatusCode().value());

            throw new AppException(ErrorCode.INVALID_INPUT,
                    "Reauthentication initiation failed. Status: " + response.getStatusCode().value());
        }

        log.info("Reauthentication initiation successful");
        return FinchUtility.parseJsonResponse(objectMapper, response.getBody(), "reauthentication initiation");

    }
}
