package com.glidingpath.finch.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.finch.dto.EmploymentDTO;
import com.glidingpath.finch.dto.IndividualDTO;
import com.glidingpath.common.util.ReflectionUtil;
import com.tryfinch.api.client.FinchClient;
import com.tryfinch.api.models.HrisEmploymentRetrieveManyPage;
import com.tryfinch.api.models.HrisEmploymentRetrieveManyParams;
import com.tryfinch.api.models.HrisIndividualRetrieveManyPage;
import com.tryfinch.api.models.HrisIndividualRetrieveManyParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class ParticipantDataHelper {
    private static final Logger logger = LoggerFactory.getLogger(ParticipantDataHelper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public IndividualDTO fetchIndividualData(FinchClient client, String individualId) {
        try {
            HrisIndividualRetrieveManyParams params = HrisIndividualRetrieveManyParams.builder()
                .requests(List.of(HrisIndividualRetrieveManyParams.Request.builder()
                    .individualId(individualId)
                    .build()))
                .build();

            HrisIndividualRetrieveManyPage response = client.hris().individuals().retrieveMany(params);

            Object responseObj = ReflectionUtil.getField(response, "response");
            Map<String, Object> responseMap = objectMapper.convertValue(responseObj, Map.class);

            List<?> responses = (List<?>) responseMap.get("responses");
            if (responses != null && !responses.isEmpty()) {
                Map<String, Object> respMap = objectMapper.convertValue(responses.get(0), Map.class);
                Object body = respMap.get("body");

                if (body != null) {
                    return objectMapper.convertValue(body, IndividualDTO.class);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch individual data from Finch API: {}", e.getMessage());
        }
        return null;
    }

    public EmploymentDTO fetchEmploymentData(FinchClient client, String individualId) {
        try {
            HrisEmploymentRetrieveManyParams params = HrisEmploymentRetrieveManyParams.builder()
                .requests(List.of(HrisEmploymentRetrieveManyParams.Request.builder()
                    .individualId(individualId)
                    .build()))
                .build();

            HrisEmploymentRetrieveManyPage response = client.hris().employments().retrieveMany(params);

            Object responseObj = ReflectionUtil.getField(response, "response");
            Map<String, Object> responseMap = objectMapper.convertValue(responseObj, Map.class);

            List<?> responses = (List<?>) responseMap.get("responses");
            if (responses != null && !responses.isEmpty()) {
                Map<String, Object> respMap = objectMapper.convertValue(responses.get(0), Map.class);
                Object body = respMap.get("body");

                if (body != null) {
                    return objectMapper.convertValue(body, EmploymentDTO.class);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch employment data from Finch API: {}", e.getMessage());
        }
        return null;
    }

    public PlanParticipant createNewParticipant(String individualId, String tenantId) {
        PlanParticipant entity = new PlanParticipant();
        entity.setIndividualId(individualId);
        entity.setTenantId(tenantId);
        entity.setIsActive(true);
        return entity;
    }

    public void updateParticipantWithIndividualData(PlanParticipant participant, IndividualDTO individual) {
        if (individual != null) {
            participant.setFirstName(individual.getFirstName());
            participant.setLastName(individual.getLastName());
            participant.setMiddleName(individual.getMiddleName());
            participant.setPreferredName(individual.getPreferredName());
            participant.setGender(individual.getGender());
            participant.setEthnicity(individual.getEthnicity());
            participant.setDob(parseDate(individual.getDob()));
        }
        participant.setIsActive(true);
    }

    public void updateParticipantWithEmploymentData(PlanParticipant participant, EmploymentDTO employment) {
        if (employment != null) {
            participant.setEmploymentStatus(employment.getEmploymentStatus());
            participant.setTitle(employment.getTitle());
            participant.setClassCode(employment.getClassCode());
            participant.setStartDate(parseDate(employment.getStartDate()));
            participant.setEndDate(parseDate(employment.getEndDate()));
        } else {
            participant.setEmploymentStatus("ACTIVE"); // Default fallback
        }
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            logger.warn("Failed to parse date: {}", dateString);
            return null;
        }
    }
} 