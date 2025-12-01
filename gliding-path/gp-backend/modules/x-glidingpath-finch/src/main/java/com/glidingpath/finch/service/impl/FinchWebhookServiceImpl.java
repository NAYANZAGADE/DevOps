package com.glidingpath.finch.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.glidingpath.common.service.FinchBenefitService;
import com.glidingpath.finch.service.FinchDataFetcherService;
import com.glidingpath.finch.helper.FinchDataSyncHelper;
import com.glidingpath.finch.helper.ParticipantDataHelper;
import com.glidingpath.finch.helper.WebhookEventProcessor;
import com.glidingpath.finch.service.FinchWebhookService;
import com.tryfinch.api.errors.FinchException;
import com.tryfinch.api.models.WebhookEvent;

@Service
public class FinchWebhookServiceImpl implements FinchWebhookService {
    private static final Logger logger = LoggerFactory.getLogger(FinchWebhookServiceImpl.class);
    private final String webhookSecret;
    private final FinchClientFactory finchClientFactory;

    @Autowired
    private FinchDataFetcherService finchDataFetcherService;
    @Autowired
    private FinchBenefitService finchBenefitService;

    // Helper classes
    @Autowired
    private WebhookEventProcessor eventProcessor;
    @Autowired
    private FinchDataSyncHelper dataSyncHelper;
    @Autowired
    private ParticipantDataHelper participantDataHelper;

    @Autowired
    public FinchWebhookServiceImpl(@Value("${finch.webhook.secret}") String webhookSecret, FinchClientFactory finchClientFactory) {
        this.webhookSecret = webhookSecret;
        this.finchClientFactory = finchClientFactory;
    }

    @Override
    public void handleWebhook(String body, Map<String, String> headers) {
        try {
            WebhookEvent event = eventProcessor.unwrapWebhookEvent(body, headers, webhookSecret, finchClientFactory);
            WebhookEventProcessor.WebhookEventData eventData = eventProcessor.extractEventData(event);
            
            if (eventData == null) {
                logger.warn("Unsupported webhook event type: {}", event.getClass().getSimpleName());
                return;
            }

            logger.info("Processing webhook event: eventType={}, connectionId={}, dataSize={}",
                eventData.getEventType(), eventData.getConnectionId(), 
                eventData.getData() != null ? eventData.getData().size() : 0);
            
            processWebhook(eventData.getEventType(), eventData.getConnectionId(), eventData.getData());
            logger.info("Webhook processed successfully: eventType={}, connectionId={}", 
                eventData.getEventType(), eventData.getConnectionId());
        } catch (FinchException e) {
            logger.error("FinchException during webhook verification or processing: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleWebhook: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error in handleWebhook: " + e.getMessage(), e);
        }
    }

    private void processWebhook(String eventType, String connectionId, JsonNode data) {
        try {
            switch (eventType) {
                // Individual events
                case "individual.created":
                case "individual.updated":
                case "individual.deleted":
                    processIndividualEvent(connectionId, data, eventType);
                    break;

                // Employment events
                case "employment.created":
                case "employment.updated":
                case "employment.deleted":
                    processIndividualEvent(connectionId, data, eventType);
                    break;

                // Company events
                case "company.created":
                case "company.updated":
                case "company.deleted":
                    processCompanyEvent(connectionId, eventType);
                    break;

                // Benefit job completion events
                case "job.benefit_create.completed":
                case "job.benefit_enroll.completed":
                case "job.benefit_update.completed":
                case "job.benefit_delete.completed":
                    processBenefitJobCompleted(connectionId, data, eventType);
                    break;

                default:
                    logger.warn("Unhandled webhook event type: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error processing webhook event: {} for connectionId: {}", eventType, connectionId, e);
        }
    }

    private void processIndividualEvent(String connectionId, JsonNode data, String eventType) {
        if (data != null && data.has("individual_id")) {
            String individualId = data.get("individual_id").asText();
            dataSyncHelper.syncDataFromFinch(connectionId, getSyncType(eventType), getAction(eventType), individualId, finchClientFactory);
        } else {
            logger.warn("Missing individual_id in webhook data for event: {}", eventType);
        }
    }

    private void processCompanyEvent(String connectionId, String eventType) {
        dataSyncHelper.syncDataFromFinch(connectionId, "COMPANY", getAction(eventType), null, finchClientFactory);
    }

    private void processBenefitJobCompleted(String connectionId, JsonNode data, String eventType) {
        String jobType = eventType.replace("job.", "").replace(".completed", "");
        dataSyncHelper.syncDataFromFinch(connectionId, "BENEFIT", "JOB_COMPLETED", null, finchClientFactory);
        if (data != null) {
            logger.info("Job completion data: {}", data.toString());
        }
    }

    private String getSyncType(String eventType) {
        if (eventType.startsWith("individual")) return "INDIVIDUAL";
        if (eventType.startsWith("employment")) return "EMPLOYMENT";
        if (eventType.startsWith("company")) return "COMPANY";
        return "UNKNOWN";
    }

    private String getAction(String eventType) {
        if (eventType.endsWith(".created")) return "CREATED";
        if (eventType.endsWith(".updated")) return "UPDATED";
        if (eventType.endsWith(".deleted")) return "DELETED";
        return "UNKNOWN";
    }

    // Event-specific handler methods
    @Override
    public void handleIndividualUpdated(String connectionId, String individualId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "INDIVIDUAL", "UPDATED", individualId, finchClientFactory);
    }

    @Override
    public void handleEmploymentUpdated(String connectionId, String individualId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "EMPLOYMENT", "UPDATED", individualId, finchClientFactory);
    }

    @Override
    public void handleCompanyUpdated(String connectionId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "COMPANY", "UPDATED", null, finchClientFactory);
    }

    @Override
    public void handleBenefitJobCompleted(String connectionId, JsonNode data, String jobType) {
        dataSyncHelper.syncDataFromFinch(connectionId, "BENEFIT", "JOB_COMPLETED", null, finchClientFactory);
        if (data != null) {
            logger.info("Job completion data: {}", data.toString());
        }
    }

    @Override
    public void handleIndividualCreated(String connectionId, String individualId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "INDIVIDUAL", "CREATED", individualId, finchClientFactory);
    }

    @Override
    public void handleIndividualDeleted(String connectionId, String individualId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "INDIVIDUAL", "DELETED", individualId, finchClientFactory);
    }

    @Override
    public void handleEmploymentCreated(String connectionId, String individualId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "EMPLOYMENT", "CREATED", individualId, finchClientFactory);
    }

    @Override
    public void handleEmploymentDeleted(String connectionId, String individualId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "EMPLOYMENT", "DELETED", individualId, finchClientFactory);
    }

    @Override
    public void handleCompanyCreated(String connectionId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "COMPANY", "CREATED", null, finchClientFactory);
    }

    @Override
    public void handleCompanyDeleted(String connectionId) {
        dataSyncHelper.syncDataFromFinch(connectionId, "COMPANY", "DELETED", null, finchClientFactory);
    }
}

