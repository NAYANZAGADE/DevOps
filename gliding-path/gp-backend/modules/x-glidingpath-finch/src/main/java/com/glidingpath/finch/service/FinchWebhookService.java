
package com.glidingpath.finch.service;

import java.util.Map;

public interface FinchWebhookService {
void handleWebhook(String body, Map<String, String> headers);

/**
* Handles an individual.updated webhook event.
*/
void handleIndividualUpdated(String connectionId, String individualId);

/**
* Handles a company.updated webhook event.
*/
void handleCompanyUpdated(String connectionId);

/**
* Handles an employment.updated webhook event.
*/
void handleEmploymentUpdated(String connectionId, String individualId);

/**
* Handles an individual.created webhook event.
*/
void handleIndividualCreated(String connectionId, String individualId);

/**
* Handles an individual.deleted webhook event.
*/
void handleIndividualDeleted(String connectionId, String individualId);

/**
* Handles an employment.created webhook event.
*/
void handleEmploymentCreated(String connectionId, String individualId);

/**
* Handles an employment.deleted webhook event.
*/
void handleEmploymentDeleted(String connectionId, String individualId);

/**
* Handles a company.created webhook event.
*/
void handleCompanyCreated(String connectionId);

/**
* Handles a company.deleted webhook event.
*/
void handleCompanyDeleted(String connectionId);



/**
* Handles benefit job completion events from Finch webhooks.
* These are the actual events that Finch sends when benefit operations complete.
*/
void handleBenefitJobCompleted(String connectionId, com.fasterxml.jackson.databind.JsonNode data, String jobType);
}