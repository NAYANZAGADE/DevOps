package com.glidingpath.finch.util;

import com.glidingpath.finch.entity.WebhookEventLog;
import com.glidingpath.finch.repository.FinchConnectionMappingRepository;
import com.glidingpath.finch.repository.WebhookEventLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FinchHelper {

    @Autowired
    private FinchConnectionMappingRepository connectionMappingRepository;

    @Autowired
    private WebhookEventLogRepository eventLogRepository;

    public String getAccessTokenForConnection(String connectionId) {
        // Lookup or map connectionId to tenant/company, then get access token
        return "ACCESS_TOKEN_FOR_" + connectionId; // Placeholder
    }

    public String getTenantIdForConnection(String connectionId) {
        return connectionMappingRepository.findByConnectionId(connectionId)
            .map(com.glidingpath.finch.entity.FinchConnectionMapping::getTenantId)
            .orElseThrow(() -> new IllegalArgumentException("No tenant found for connectionId: " + connectionId));
    }

    public void logWebhookEvent(String eventId, String requestBody, String responseBody) {
        WebhookEventLog log = new WebhookEventLog();
        log.setEventId(eventId);
        log.setProcessedAt(java.time.Instant.now());
        log.setRequestBody(requestBody);
        log.setResponseBody(responseBody);
        eventLogRepository.save(log);
    }

    public boolean isWebhookEventProcessed(String eventId) {
        return eventLogRepository.existsByEventId(eventId);
    }
} 