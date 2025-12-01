package com.glidingpath.finch.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glidingpath.finch.dto.DirectoryDTO;
import com.glidingpath.finch.dto.EmploymentDTO;
import com.glidingpath.finch.dto.IndividualDTO;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.finch.exception.FinchException;
import com.glidingpath.finch.mapping.FinchMapping;
import com.glidingpath.finch.mapping.FinchSdkToDtoMapper;
import com.glidingpath.finch.service.impl.FinchClientFactory;
import com.glidingpath.common.util.ReflectionUtil;
import com.tryfinch.api.client.FinchClient;
import com.tryfinch.api.models.HrisEmploymentRetrieveManyParams;
import com.tryfinch.api.models.HrisEmploymentRetrieveManyPage;
import com.tryfinch.api.models.HrisIndividualRetrieveManyParams;
import com.tryfinch.api.models.HrisIndividualRetrieveManyPage;
import com.tryfinch.api.models.IndividualInDirectory;
import com.tryfinch.api.models.HrisDirectoryListPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class FinchEmployeeProcessor implements ItemProcessor<IndividualInDirectory, PlanParticipant> {

    @Autowired
    private FinchClientFactory finchClientFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private String tenantId;
    private FinchClient client;
    
    // Pre-fetched data cache
    private Map<String, IndividualDTO> individualCache = new ConcurrentHashMap<>();
    private Map<String, EmploymentDTO> employmentCache = new ConcurrentHashMap<>();
    
    private static final int BATCH_SIZE = 100;

    public void initialize(String tenantId) throws Exception {
        this.tenantId = tenantId;
        this.client = finchClientFactory.createClient(tenantId);
    }

    public void resetForNewJob() {
        this.individualCache.clear();
        this.employmentCache.clear();
        log.debug("Reset processor caches for new job execution");
    }

    @Override
    public PlanParticipant process(IndividualInDirectory directory) throws Exception {
        if (client == null) {
            throw FinchException.initializationError("Processor not initialized. Call initialize() first.");
        }

        String employeeId = directory.id();

        // Trigger pre-fetch only once on first employee
        if (individualCache.isEmpty() && employmentCache.isEmpty()) {
            preFetchAllEmployeeData();
        }

        // Get data from cache
        IndividualDTO individual = individualCache.get(employeeId);
        EmploymentDTO employment = employmentCache.get(employeeId);

        if (individual == null) {
            log.warn("No individual data found for employee: {}", employeeId);
            throw FinchException.dataNotFound("Failed to fetch individual data for employee: " + employeeId);
        }

        if (employment == null) {
            log.warn("No employment data found for employee: {}", employeeId);
            throw FinchException.dataNotFound("Failed to fetch employment data for employee: " + employeeId);
        }

        try {
            // Map to entity
            DirectoryDTO directoryDTO = FinchSdkToDtoMapper.toDirectoryDTO(directory);
            return FinchMapping.toEntity(directoryDTO, individual, employment, tenantId);
        } catch (Exception e) {
            log.error("Unexpected error while processing employee: {}", employeeId, e);
            throw FinchException.mappingError("Failed to process employee: " + employeeId + " - " + e.getMessage());
        }
    }

    private void preFetchAllEmployeeData() {
        try {
            // Get all employee IDs from the directory
            List<String> allEmployeeIds = getAllEmployeeIds();
            
            if (allEmployeeIds.isEmpty()) {
                log.warn("No employees found in directory for tenantId: {}", tenantId);
                return;
            }
            
            // Fetch all individual data in batches
            fetchIndividualDataInBatches(allEmployeeIds);
            
            // Fetch all employment data in batches
            fetchEmploymentDataInBatches(allEmployeeIds);
            
        } catch (Exception e) {
            log.error("Failed to pre-fetch employee data", e);
            throw FinchException.cacheError("Failed to pre-fetch employee data: " + e.getMessage());
        }
    }

    private List<String> getAllEmployeeIds() throws Exception {
        HrisDirectoryListPage directoryPage = client.hris().directory().list();
        Set<String> employeeIds = new HashSet<>();
        
        for (IndividualInDirectory employee : directoryPage.autoPager()) {
            employeeIds.add(employee.id());
        }
        
        return new ArrayList<>(employeeIds);
    }

    private void fetchIndividualDataInBatches(List<String> employeeIds) { // Fetches individual data from Finch API in batches
        for (int i = 0; i < employeeIds.size(); i += BATCH_SIZE) {
            List<String> batch = employeeIds.subList(i, Math.min(i + BATCH_SIZE, employeeIds.size()));

            try {
                HrisIndividualRetrieveManyParams params = HrisIndividualRetrieveManyParams.builder()
                    .requests(batch.stream()
                        .map(id -> HrisIndividualRetrieveManyParams.Request.builder().individualId(id).build())
                        .toList())
                    .build();

                HrisIndividualRetrieveManyPage response = client.hris().individuals().retrieveMany(params);
                processIndividualResponse(response); // Processes individual data response and caches it
                
            } catch (Exception e) {
                log.error("Failed to fetch individual batch {}-{}", i, i + batch.size(), e);
                throw FinchException.apiError("Failed to fetch individual data batch", e);
            }
        }
    }

    private void processIndividualResponse(HrisIndividualRetrieveManyPage response) { // Processes individual data response and stores in cache
        Object responseObj = ReflectionUtil.getField(response, "response");
        Map<String, Object> responseMap = objectMapper.convertValue(responseObj, Map.class);
        
        List<?> responses = (List<?>) responseMap.get("responses");
        if (responses == null) {
            return;
        }
        
        for (Object resp : responses) {
            Map<String, Object> respMap = objectMapper.convertValue(resp, Map.class);
            Object body = respMap.get("body");
            
            if (body != null) {
                try {
                    IndividualDTO individual = objectMapper.convertValue(body, IndividualDTO.class);
                    individualCache.put(individual.getId(), individual); // Stores individual data in cache
                } catch (Exception conversionError) {
                    log.error("Failed to convert body to IndividualDTO: {}", conversionError.getMessage());
                }
            }
        }
    }

    private void fetchEmploymentDataInBatches(List<String> employeeIds) { // Fetches employment data from Finch API in batches
        for (int i = 0; i < employeeIds.size(); i += BATCH_SIZE) {
            List<String> batch = employeeIds.subList(i, Math.min(i + BATCH_SIZE, employeeIds.size()));

            try {
                HrisEmploymentRetrieveManyParams params = HrisEmploymentRetrieveManyParams.builder()
                    .requests(batch.stream()
                        .map(id -> HrisEmploymentRetrieveManyParams.Request.builder().individualId(id).build())
                        .toList())
                    .build();

                HrisEmploymentRetrieveManyPage response = client.hris().employments().retrieveMany(params);
                processEmploymentResponse(response); // Processes employment data response and caches it
                
            } catch (Exception e) {
                log.error("Failed to fetch employment batch {}-{}", i, i + batch.size(), e);
                throw FinchException.apiError("Failed to fetch employment data batch", e);
            }
        }
    }

    private void processEmploymentResponse(HrisEmploymentRetrieveManyPage response) { // Processes employment data response and stores in cache
        Object responseObj = ReflectionUtil.getField(response, "response");
        Map<String, Object> responseMap = objectMapper.convertValue(responseObj, Map.class);
        
        List<?> responses = (List<?>) responseMap.get("responses");
        if (responses == null) {
            return;
        }
        
        for (Object resp : responses) {
            Map<String, Object> respMap = objectMapper.convertValue(resp, Map.class);
            Object body = respMap.get("body");
            String individualId = (String) respMap.get("individual_id");
            
            if (body != null && individualId != null) {
                try {
                    EmploymentDTO employment = objectMapper.convertValue(body, EmploymentDTO.class);
                    employment.setIndividualId(individualId);
                    employmentCache.put(individualId, employment); // Stores employment data in cache
                } catch (Exception conversionError) {
                    log.error("Failed to convert body to EmploymentDTO: {}", conversionError.getMessage());
                }
            }
        }
    }

    public void flushBatchData() {
        // With pre-fetch strategy, all data should already be in cache
        // This method is kept for compatibility but should not be needed
    }
} 