package com.glidingpath.finch.service.impl;
import com.glidingpath.finch.dto.FinchUrlDTO;
import com.glidingpath.finch.service.FinchUrlService;
import com.glidingpath.finch.config.FinchConfig;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FinchUrlServiceImpl implements FinchUrlService {

    private final FinchConfig finchConfig;
    private final RestTemplate restTemplate;

    @Override
    public FinchUrlDTO generateFinchConnectUrl(String tenantId) {
        String sessionUrl = finchConfig.getSessionUrl();

        Map<String, Object> body = new HashMap<>();
        body.put("customer_id", tenantId);
        body.put("customer_name", tenantId);
        body.put("products", Arrays.asList("company","directory", "individual", "employment","payment","benefits"));
        body.put("redirect_uri", finchConfig.getRedirectUri());
        body.put("sandbox", "finch");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(finchConfig.getClientId(), finchConfig.getClientSecret());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(sessionUrl, entity, (Class<Map<String, Object>>)(Class<?>)Map.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Failed to create Finch session");
        }

        Object connectUrlObj = response.getBody().get("connect_url");
        if (!(connectUrlObj instanceof String)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "connect_url missing from Finch session response");
        }
        String connectUrl = (String) connectUrlObj;
        return new FinchUrlDTO(connectUrl);
    }
}