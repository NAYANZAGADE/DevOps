package com.glidingpath.finch.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.entity.PlanSponsor;
import com.glidingpath.core.repository.PlanParticipantRepository;
import com.glidingpath.core.repository.PlanSponsorRepository;
import com.glidingpath.finch.dto.EmploymentDTO;
import com.glidingpath.common.dto.FinchBenefitDTO;
import com.glidingpath.finch.dto.IndividualDTO;
import com.glidingpath.common.service.FinchBenefitService;
import com.glidingpath.finch.service.impl.FinchClientFactory;
import com.glidingpath.finch.util.FinchHelper;
import com.tryfinch.api.client.FinchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FinchDataSyncHelper {
    private static final Logger logger = LoggerFactory.getLogger(FinchDataSyncHelper.class);

    @Autowired
    private FinchHelper finchHelper;
    @Autowired
    private PlanParticipantRepository planParticipantRepository;
    @Autowired
    private PlanSponsorRepository planSponsorRepository;
    @Autowired
    private FinchBenefitService finchBenefitService;
    @Autowired
    private ParticipantDataHelper participantDataHelper;

    public void syncDataFromFinch(String connectionId, String syncType, String action, String individualId, FinchClientFactory finchClientFactory) {
        try {
            String tenantId = finchHelper.getTenantIdForConnection(connectionId);
            FinchClient client = finchClientFactory.createClient(tenantId);

            logger.info("Starting {} {} for tenantId: {}, connectionId: {}", action, syncType, tenantId, connectionId);

            SyncOperation operation = getSyncOperation(syncType, action);
            if (operation != null) {
                operation.execute(client, individualId, tenantId);
                logger.info("Completed {} {} for tenantId: {}", action, syncType, tenantId);
            } else {
                logger.warn("Unknown sync type/action: {}/{}", syncType, action);
            }

        } catch (Exception e) {
            logger.error("Error during {} {} for connectionId: {}: {}", action, syncType, connectionId, e.getMessage(), e);
        }
    }

    private SyncOperation getSyncOperation(String syncType, String action) {
        String key = syncType.toUpperCase() + "_" + action.toUpperCase();

        switch (key) {
            // UPDATE operations
            case "INDIVIDUAL_UPDATED":
                return (client, individualId, tenantId) -> {
                    IndividualDTO individual = participantDataHelper.fetchIndividualData(client, individualId);
                    PlanParticipant participant = planParticipantRepository.findByIndividualId(individualId)
                        .orElseGet(() -> participantDataHelper.createNewParticipant(individualId, tenantId));

                    participantDataHelper.updateParticipantWithIndividualData(participant, individual);
                    planParticipantRepository.save(participant);

                    logger.info("{} individual data for participant: {}",
                        participant.getId() == null ? "Created" : "Updated", individualId);
                };

            case "EMPLOYMENT_UPDATED":
                return (client, individualId, tenantId) -> {
                    EmploymentDTO employment = participantDataHelper.fetchEmploymentData(client, individualId);
                    PlanParticipant existing = planParticipantRepository.findByIndividualId(individualId).orElse(null);

                    if (existing != null) {
                        participantDataHelper.updateParticipantWithEmploymentData(existing, employment);
                        planParticipantRepository.save(existing);
                        logger.info("Updated employment data for participant: {}", individualId);
                    } else {
                        logger.warn("No participant found for individualId: {} when syncing employment", individualId);
                    }
                };

            case "COMPANY_UPDATED":
                return (client, individualId, tenantId) -> {
                    var company = client.hris().company().retrieve();
                    PlanSponsor existing = planSponsorRepository.findByTenantId(tenantId).orElse(null);

                    if (existing != null) {
                        existing.setLegalName(company.legalName().orElse(null));
                        if (company.entity().isPresent()) {
                            existing.setEntityType(company.entity().get().type().orElse(null).toString());
                        }
                        planSponsorRepository.save(existing);
                        logger.info("Updated existing company for tenantId: {}", tenantId);
                    } else {
                        PlanSponsor entity = new PlanSponsor();
                        entity.setTenantId(tenantId);
                        entity.setLegalName(company.legalName().orElse(null));
                        if (company.entity().isPresent()) {
                            entity.setEntityType(company.entity().get().type().orElse(null).toString());
                        }
                        planSponsorRepository.save(entity);
                        logger.info("Created new company for tenantId: {}", tenantId);
                    }
                };

            case "INDIVIDUAL_CREATED":
                return (client, individualId, tenantId) -> {
                    IndividualDTO individual = participantDataHelper.fetchIndividualData(client, individualId);
                    PlanParticipant participant = participantDataHelper.createNewParticipant(individualId, tenantId);
                    participantDataHelper.updateParticipantWithIndividualData(participant, individual);
                    planParticipantRepository.save(participant);
                    logger.info("Created new participant for individual: {}", individualId);
                };

            case "EMPLOYMENT_CREATED":
                return (client, individualId, tenantId) -> {
                    EmploymentDTO employment = participantDataHelper.fetchEmploymentData(client, individualId);
                    PlanParticipant existing = planParticipantRepository.findByIndividualId(individualId).orElse(null);

                    if (existing != null) {
                        participantDataHelper.updateParticipantWithEmploymentData(existing, employment);
                        planParticipantRepository.save(existing);
                        logger.info("Updated participant with new employment data: {}", individualId);
                    } else {
                        logger.warn("No participant found for employment creation: {}", individualId);
                    }
                };

            case "COMPANY_CREATED":
                return (client, individualId, tenantId) -> {
                    var company = client.hris().company().retrieve();
                    PlanSponsor entity = new PlanSponsor();
                    entity.setTenantId(tenantId);
                    entity.setLegalName(company.legalName().orElse(null));
                    if (company.entity().isPresent()) {
                        entity.setEntityType(company.entity().get().type().orElse(null).toString());
                    }
                    planSponsorRepository.save(entity);
                    logger.info("Created new company sponsor for tenantId: {}", tenantId);
                };

            // DELETE operations
            case "INDIVIDUAL_DELETED":
                return (client, individualId, tenantId) -> {
                    PlanParticipant existing = planParticipantRepository.findByIndividualId(individualId).orElse(null);
                    if (existing != null) {
                        existing.setIsActive(false);
                        planParticipantRepository.save(existing);
                        logger.info("Deactivated participant for individual: {}", individualId);
                    } else {
                        logger.warn("No participant found for deleted individual: {}", individualId);
                    }
                };

            case "EMPLOYMENT_DELETED":
                return (client, individualId, tenantId) -> {
                    PlanParticipant existing = planParticipantRepository.findByIndividualId(individualId).orElse(null);
                    if (existing != null) {
                        existing.setEmploymentStatus("TERMINATED");
                        planParticipantRepository.save(existing);
                        logger.info("Updated participant employment status to terminated: {}", individualId);
                    } else {
                        logger.warn("No participant found for employment deletion: {}", individualId);
                    }
                };

            case "COMPANY_DELETED":
                return (client, individualId, tenantId) -> {
                    PlanSponsor existing = planSponsorRepository.findByTenantId(tenantId).orElse(null);
                    if (existing != null) {
                        logger.info("Company sponsor found for deletion, tenantId: {}", tenantId);
                    } else {
                        logger.warn("No company sponsor found for deletion: {}", tenantId);
                    }
                };

            // JOB COMPLETION operations
            case "BENEFIT_JOB_COMPLETED":
                return (client, individualId, tenantId) -> {
                    try {
                        List<FinchBenefitDTO> benefits = finchBenefitService.getAllBenefits(tenantId);
                        logger.info("Synced benefits after job completion for tenantId: {}, found {} benefits", tenantId, benefits.size());
                    } catch (Exception e) {
                        logger.error("Error processing BENEFIT_JOB_COMPLETED for tenantId: {}", tenantId, e);
                    }
                };

            default:
                return null;
        }
    }

    @FunctionalInterface
    private interface SyncOperation {
        void execute(FinchClient client, String individualId, String tenantId);
    }
} 