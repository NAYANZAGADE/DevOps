package com.glidingpath.finch.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glidingpath.finch.service.impl.FinchClientFactory;
import com.tryfinch.api.core.http.Headers;
import com.tryfinch.api.errors.FinchException;
import com.tryfinch.api.models.WebhookEvent;
import com.tryfinch.api.services.blocking.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebhookEventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(WebhookEventProcessor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebhookEvent unwrapWebhookEvent(String body, Map<String, String> headers, String webhookSecret, FinchClientFactory finchClientFactory) throws FinchException {
        try {
            Headers.Builder builder = Headers.builder();
            headers.forEach(builder::put);
            Headers finchHeaders = builder.build();
            WebhookService webhookService = finchClientFactory.createClient("").webhooks();
            return webhookService.unwrap(body, finchHeaders, webhookSecret);
        } catch (Exception e) {
            logger.error("Error unwrapping webhook event: {}", e.getMessage(), e);
            throw new FinchException("Failed to unwrap webhook event", e);
        }
    }

    public WebhookEventData extractEventData(WebhookEvent event) {
        String eventType = null;
        String connectionId = null;
        Object data = null;

        if (event.isCompanyUpdated()) {
            var companyEvent = event.asCompanyUpdated();
            eventType = companyEvent.eventType().map(Object::toString).orElse(null);
            connectionId = companyEvent.connectionId().orElse(null);
            data = companyEvent.data().orElse(null);
        } else if (event.isIndividual()) {
            var individualEvent = event.asIndividual();
            eventType = individualEvent.eventType().map(Object::toString).orElse(null);
            connectionId = individualEvent.connectionId().orElse(null);
            data = individualEvent.data().orElse(null);
        } else if (event.isEmployment()) {
            var employmentEvent = event.asEmployment();
            eventType = employmentEvent.eventType().map(Object::toString).orElse(null);
            connectionId = employmentEvent.connectionId().orElse(null);
            data = employmentEvent.data().orElse(null);
        } else if (event.isJobCompletion()) {
            var jobEvent = event.asJobCompletion();
            eventType = jobEvent.eventType().map(Object::toString).orElse(null);
            connectionId = jobEvent.connectionId().orElse(null);
            data = jobEvent.data().orElse(null);
        }

        if (eventType != null && connectionId != null) {
            JsonNode dataNode = objectMapper.valueToTree(data);
            return new WebhookEventData(eventType, connectionId, dataNode);
        }
        return null;
    }

    public static class WebhookEventData {
        private final String eventType;
        private final String connectionId;
        private final JsonNode data;

        public WebhookEventData(String eventType, String connectionId, JsonNode data) {
            this.eventType = eventType;
            this.connectionId = connectionId;
            this.data = data;
        }

        public String getEventType() { return eventType; }
        public String getConnectionId() { return connectionId; }
        public JsonNode getData() { return data; }
    }
} 