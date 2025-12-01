package com.glidingpath.finch.service.impl;

import com.glidingpath.common.dto.FinchBenefitDTO;
import com.glidingpath.common.service.FinchBenefitService;
import com.glidingpath.finch.service.TokenManager;
import com.glidingpath.finch.service.impl.FinchClientFactory;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import com.glidingpath.finch.repository.BenefitRepository;
import com.glidingpath.core.entity.Benefit;
import com.tryfinch.api.client.FinchClient;
import com.tryfinch.api.core.JsonValue;
import com.tryfinch.api.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clean implementation of FinchBenefitService following the FinchDataFetcherServiceImpl pattern.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinchBenefitServiceImpl implements FinchBenefitService {
    
    private final FinchClientFactory finchClientFactory;
    private final TokenManager tokenManager;
    private final BenefitRepository benefitRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<FinchBenefitDTO> getAllBenefits(String tenantId) throws Exception {
        log.info("Fetching all benefits for tenantId={}", tenantId);
        try {
            FinchClient client = finchClientFactory.createClient(tenantId);
            var benefitsResponse = client.hris().benefits().list();
            return benefitsResponse.items().stream()
                    .map(benefit -> objectMapper.convertValue(benefit, FinchBenefitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching benefits for tenantId={}", tenantId, e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to fetch all benefits", e);
        }
    }

    @Override
    public FinchBenefitDTO getBenefitById(String tenantId, String benefitId) throws Exception {
        log.info("Fetching benefit by id for tenantId={}, benefitId={}", tenantId, benefitId);
        try {
            FinchClient client = finchClientFactory.createClient(tenantId);
            var benefit = client.hris().benefits().retrieve(benefitId);
            return objectMapper.convertValue(benefit, FinchBenefitDTO.class);
        } catch (Exception e) {
            log.error("Error fetching benefit by id for tenantId={}, benefitId={}", tenantId, benefitId, e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to fetch benefit by id", e);
        }
    }

            @Override
    public Map<String, String> createDeduction(String tenantId, Map<String, Object> requestBody) throws Exception {
        log.info("Creating deduction for tenantId={}", tenantId);
        try {
            FinchClient client = finchClientFactory.createClient(tenantId);
            HrisBenefitCreateParams params = buildCreateParams(requestBody);
            CreateCompanyBenefitsResponse response = client.hris().benefits().create(params);
            
            Map<String, String> result = createResponseMap(response);
            saveBenefitToDatabase(tenantId, result.get("benefit_id"), requestBody);
            return result;
            
        } catch (Exception e) {
            handleBenefitCreationError(e, tenantId);
            throw e;
        }
    }

            @Override
    public Map<String, String> registerDeduction(String tenantId, Map<String, Object> requestBody) throws Exception {
        // Same as createDeduction for simplicity
        return createDeduction(tenantId, requestBody);
    }

            @Override
    public Map<String, String> updateBenefit(String tenantId, String benefitId, Map<String, Object> requestBody) throws Exception {
        log.info("Updating benefit for tenantId={}, benefitId={}", tenantId, benefitId);
        try {
            FinchClient client = finchClientFactory.createClient(tenantId);
            HrisBenefitUpdateParams params = buildUpdateParams(requestBody);
            UpdateCompanyBenefitResponse response = client.hris().benefits().update(benefitId, params);
            return createResponseMap(response);
        } catch (Exception e) {
            log.error("Error updating benefit for tenantId={}, benefitId={}: {}", tenantId, benefitId, e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to update benefit", e);
        }
    }

            @Override
    public Map<String, Object> getEnrolledIndividuals(String tenantId, String benefitId) throws Exception {
        log.info("Getting enrolled individuals for tenantId={}, benefitId={}", tenantId, benefitId);
        try {
            FinchClient client = finchClientFactory.createClient(tenantId);
            IndividualEnrolledIdsResponse response = client.hris().benefits().individuals().enrolledIds(benefitId);
            return new HashMap<>(response._additionalProperties());
        } catch (Exception e) {
            log.error("Error getting enrolled individuals for tenantId={}, benefitId={}: {}", tenantId, benefitId, e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to get enrolled individuals", e);
        }
    }

            @Override
    public List<Map<String, Object>> getIndividualDeductions(String tenantId, String benefitId, List<String> individualIds) throws Exception {
        log.info("Getting individual deductions for tenantId={}, benefitId={}", tenantId, benefitId);
        try {
            FinchClient client = finchClientFactory.createClient(tenantId);
            HrisBenefitIndividualRetrieveManyBenefitsParams params = buildIndividualParams(individualIds);
            HrisBenefitIndividualRetrieveManyBenefitsPage page = client.hris().benefits().individuals().retrieveManyBenefits(benefitId, params);
            
            return page.items().stream()
                .map(item -> (Map<String, Object>) objectMapper.convertValue(item, Map.class))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting individual deductions for tenantId={}, benefitId={}: {}", tenantId, benefitId, e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to get individual deductions", e);
        }
    }

            @Override
    public Map<String, Object> enrollIndividualsInDeduction(String tenantId, String benefitId, List<Map<String, Object>> enrollments) throws Exception {
        log.info("Enrolling individuals for tenantId={}, benefitId={}", tenantId, benefitId);
        try {
            List<Map<String, Object>> transformedEnrollments = transformEnrollments(enrollments);
            ResponseEntity<Map> response = makeHttpRequest("/employer/benefits/" + benefitId + "/individuals", 
                transformedEnrollments, tenantId, HttpMethod.POST);
            
            Map<String, Object> result = new HashMap<>();
            result.put("job_id", response.getBody().get("job_id"));
            updateBenefitInDatabase(benefitId, tenantId, "enrollment", enrollments.size());
            return result;
            
        } catch (Exception e) {
            log.error("Error enrolling individuals for tenantId={}, benefitId={}: {}", tenantId, benefitId, e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to enroll individuals", e);
        }
    }

            @Override
    public Map<String, Object> unenrollIndividualsFromDeduction(String tenantId, String benefitId, List<String> individualIds) throws Exception {
        log.info("Unenrolling individuals for tenantId={}, benefitId={}", tenantId, benefitId);
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("individual_ids", individualIds);
            ResponseEntity<Map> response = makeHttpRequest("/employer/benefits/" + benefitId + "/individuals", 
                body, tenantId, HttpMethod.DELETE);
            
            Map<String, Object> result = new HashMap<>();
            result.put("job_id", response.getBody().get("job_id"));
            updateBenefitInDatabase(benefitId, tenantId, "unenrollment", individualIds.size());
            return result;
            
        } catch (Exception e) {
            log.error("Error unenrolling individuals for tenantId={}, benefitId={}: {}", tenantId, benefitId, e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to unenroll individuals", e);
        }
    }
    
    // Helper methods
    private HrisBenefitCreateParams buildCreateParams(Map<String, Object> requestBody) {
        HrisBenefitCreateParams.Builder builder = HrisBenefitCreateParams.builder()
            .type(BenefitType.of((String) requestBody.get("type")))
            .description((String) requestBody.get("description"))
            .frequency(BenefitFrequency.of((String) requestBody.get("frequency")));

        if (requestBody.containsKey("company_contribution")) {
            builder.putAdditionalBodyProperty("company_contribution", JsonValue.from(requestBody.get("company_contribution")));
        }
        return builder.build();
    }

    private HrisBenefitUpdateParams buildUpdateParams(Map<String, Object> requestBody) {
        HrisBenefitUpdateParams.Builder builder = HrisBenefitUpdateParams.builder();
        if (requestBody.containsKey("description")) {
            builder.description((String) requestBody.get("description"));
        }
        return builder.build();
    }

    private HrisBenefitIndividualRetrieveManyBenefitsParams buildIndividualParams(List<String> individualIds) {
        HrisBenefitIndividualRetrieveManyBenefitsParams.Builder builder = HrisBenefitIndividualRetrieveManyBenefitsParams.builder();
        if (individualIds != null && !individualIds.isEmpty()) {
            builder.individualIds(String.join(",", individualIds));
        }
        return builder.build();
    }

    private List<Map<String, Object>> transformEnrollments(List<Map<String, Object>> enrollments) {
        return enrollments.stream()
            .map(enrollment -> {
                Map<String, Object> transformed = new HashMap<>(enrollment);
                if (!transformed.containsKey("configuration")) {
                    Map<String, Object> configuration = new HashMap<>();
                    if (transformed.containsKey("contribution")) {
                        configuration.put("contribution", transformed.get("contribution"));
                        transformed.remove("contribution");
                    }
                    transformed.put("configuration", configuration);
                }
                return transformed;
            })
            .collect(Collectors.toList());
    }

    private ResponseEntity<Map> makeHttpRequest(String endpoint, Object body, String tenantId, HttpMethod method) throws Exception {
        String url = "https://api.tryfinch.com" + endpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tokenManager.getValidAccessToken(tenantId));
        headers.set("Finch-API-Version", "2020-09-17");
        
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, entity, Map.class);
    }

    private Map<String, String> createResponseMap(Object response) {
        Map<String, String> result = new HashMap<>();
        result.put("job_id", extractField(response, "jobId"));
        result.put("benefit_id", extractField(response, "benefitId"));
        return result;
    }

    private String extractField(Object response, String fieldName) {
        try {
            java.lang.reflect.Field field = response.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(response);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.warn("Could not extract {} via reflection: {}", fieldName, e.getMessage());
            return null;
        }
    }
    
    private void handleBenefitCreationError(Exception e, String tenantId) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("not_supported_by_provider")) {
            throw new AppException(ErrorCode.INTERNAL_ERROR, "This feature is not supported by your payroll provider. Please remove company match or try a different provider.");
        } else if (msg != null && msg.contains("invalid_request")) {
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Invalid request format. Please check the request parameters and try again.");
        }
        log.error("Error creating benefit for tenantId={}: {}", tenantId, e.getMessage(), e);
        throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to create benefit", e);
    }

    private void saveBenefitToDatabase(String tenantId, String benefitId, Map<String, Object> requestBody) {
        try {
            Benefit entity = new Benefit();
            entity.setBenefitId(benefitId);
            entity.setTenantId(tenantId);
            entity.setType((String) requestBody.get("type"));
            entity.setDescription((String) requestBody.get("description"));
            entity.setFrequency((String) requestBody.get("frequency"));
            
            if (requestBody.containsKey("company_contribution")) {
                Map<String, Object> cc = (Map<String, Object>) requestBody.get("company_contribution");
                Benefit.CompanyContribution companyContribution = new Benefit.CompanyContribution();
                companyContribution.setType((String) cc.get("type"));
                entity.setCompanyContribution(companyContribution);
            }
            
            benefitRepository.save(entity);
            log.info("Saved benefit to database for tenantId={}, benefitId={}", tenantId, benefitId);
        } catch (Exception e) {
            log.warn("Failed to save benefit to database for tenantId={}, benefitId={}: {}", tenantId, benefitId, e.getMessage());
        }
    }

    private void updateBenefitInDatabase(String benefitId, String tenantId, String operation, int count) {
        try {
            Optional<Benefit> existingBenefit = benefitRepository.findByBenefitIdAndTenantId(benefitId, tenantId);
            if (existingBenefit.isPresent()) {
                benefitRepository.save(existingBenefit.get());
                log.info("Updated benefit in database after {} for tenantId={}, benefitId={}, count={}", 
                    operation, tenantId, benefitId, count);
            }
        } catch (Exception e) {
            log.warn("Failed to update benefit in database for {} tenantId={}, benefitId={}: {}", 
                operation, tenantId, benefitId, e.getMessage());
        }
    }
}