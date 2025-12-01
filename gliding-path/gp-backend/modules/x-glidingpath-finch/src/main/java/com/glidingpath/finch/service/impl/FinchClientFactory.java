package com.glidingpath.finch.service.impl;

import org.springframework.stereotype.Component;

import com.glidingpath.finch.service.TokenManager;
import com.tryfinch.api.client.FinchClient;
import com.tryfinch.api.client.okhttp.FinchOkHttpClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FinchClientFactory {

	private final TokenManager tokenManager;

    public FinchClient createClient(String tenantId) throws Exception {
       String accessToken = tokenManager.getValidAccessToken(tenantId);

        return FinchOkHttpClient.builder()
                .accessToken(accessToken)
                .build();
    }
}