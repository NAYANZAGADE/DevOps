package com.glidingpath.finch.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

public interface FinchService {
    JsonNode exchangeCodeForTokens(String code) throws IOException;
    JsonNode refreshAccessToken(String refreshToken) throws IOException;
    JsonNode initiateReauthentication(String connectionId, String customerId) throws IOException;
}