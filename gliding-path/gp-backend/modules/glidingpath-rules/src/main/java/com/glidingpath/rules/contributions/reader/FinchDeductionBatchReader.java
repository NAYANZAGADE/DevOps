package com.glidingpath.rules.contributions.reader;

import com.glidingpath.common.dto.PrePayrollCalculationDTO;
import com.glidingpath.core.entity.PrePayrollCalculation;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.repository.PrePayrollCalculationRepository;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinchDeductionBatchReader implements ItemReader<PrePayrollCalculationDTO> {

    private final PrePayrollCalculationRepository prePayrollCalculationRepository;
    
    private String tenantId;
    private ListItemReader<PrePayrollCalculationDTO> delegateReader;
    private boolean initialized = false;
    private StepExecution stepExecution;
    
    // Thread-safe counters for monitoring
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        
        log.info("Initializing FinchDeductionBatchReader for tenant: {}", tenantId);
        
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is required for Finch deduction batch processing");
        }
        
        // Reset counters for new step execution
        processedCount.set(0);
        totalCount.set(0);
        
        initializeReader();
    }

    private void initializeReader() {
        try {
            // Try to get calculation results from job execution context first
            List<PrePayrollCalculationDTO> calculationResults = getCalculationResultsFromContext();
            
            if (calculationResults == null || calculationResults.isEmpty()) {
                // Fallback: fetch calculation results from database
                log.warn("No calculation results found in job context, fetching from database for tenant: {}", tenantId);
                calculationResults = fetchCalculationResultsFromDatabase();
            }
            
            if (calculationResults.isEmpty()) {
                log.warn("No calculation results found for Finch deduction in tenant: {} - deduction batch will process 0 records", tenantId);
                this.delegateReader = new ListItemReader<>(List.of());
                this.totalCount.set(0);
            } else {
                // Filter only successful calculations for Finch deduction
                List<PrePayrollCalculationDTO> successfulCalculations = calculationResults.stream()
                    .filter(calc -> "SUCCESS".equals(calc.getStatus()))
                    .collect(Collectors.toList());
                
                log.info("Found {} successful calculations for Finch deduction in tenant: {} (out of {} total)", 
                        successfulCalculations.size(), tenantId, calculationResults.size());
                
                this.delegateReader = new ListItemReader<>(successfulCalculations);
                this.totalCount.set(successfulCalculations.size());
            }
            
            this.initialized = true;
            
        } catch (Exception e) {
            log.error("Failed to initialize Finch deduction batch reader for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to initialize Finch deduction batch reader", e);
        }
    }

    /**
     * Get calculation results from job execution context (from previous calculation step)
     * Now expects calculation IDs and fetches full DTOs from database
     */
    @SuppressWarnings("unchecked")
    private List<PrePayrollCalculationDTO> getCalculationResultsFromContext() {
        try {
            Object contextValue = stepExecution.getJobExecution().getExecutionContext()
                .get(BatchConstants.CONTEXT_CALCULATION_RESULTS);
            
            if (contextValue instanceof List) {
                List<String> calculationIds = (List<String>) contextValue;
                
                if (!calculationIds.isEmpty()) {
                    log.info("Retrieved {} calculation IDs from job execution context, fetching full DTOs from database", calculationIds.size());
                    return fetchCalculationResultsByIds(calculationIds);
                } else {
                    log.warn("Job execution context contains empty calculation IDs list");
                }
            } else {
                log.warn("Job execution context does not contain calculation results");
            }
            
        } catch (Exception e) {
            log.warn("Failed to retrieve calculation results from job context: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Fetch calculation results from database using calculation IDs
     */
    private List<PrePayrollCalculationDTO> fetchCalculationResultsByIds(List<String> calculationIds) {
        try {
            List<PrePayrollCalculation> calculations = prePayrollCalculationRepository.findByCalculationIdIn(calculationIds);
            
            if (calculations.isEmpty()) {
                log.warn("No calculations found for the provided IDs: {}", calculationIds);
                return new ArrayList<>();
            }
            
            List<PrePayrollCalculationDTO> calculationDtos = calculations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            log.info("Successfully fetched {} calculation DTOs from database for deduction processing", calculationDtos.size());
            return calculationDtos;
            
        } catch (Exception e) {
            log.error("Failed to fetch calculation results by IDs from database for tenant: {}", tenantId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Fallback: fetch calculation results from database
     * This should only be used if the job context is empty
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    private List<PrePayrollCalculationDTO> fetchCalculationResultsFromDatabase() {
        try {
            log.warn("FALLBACK: Fetching calculation results from database - this indicates a data flow issue");
            
            // Get all successful calculations for the tenant with employee relationship eagerly loaded
            log.info("CALLING: findByTenantIdAndStatus for tenant: {}", tenantId);
            List<PrePayrollCalculation> allCalculations = prePayrollCalculationRepository.findByTenantIdAndStatus(
                tenantId, com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus.SUCCESS);
            log.info("FETCHED: {} entities from database", allCalculations.size());
            
            // Convert entities to DTOs
            log.info("CONVERTING: {} entities to DTOs", allCalculations.size());
            List<PrePayrollCalculationDTO> calculationDtos = allCalculations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            log.info("CONVERTED: {} DTOs", calculationDtos.size());
            
            log.info("FALLBACK: Fetched {} successful calculations from database for tenant: {}", calculationDtos.size(), tenantId);
            return calculationDtos;
            
        } catch (Exception e) {
            log.error("Failed to fetch calculation results from database for tenant: {}", tenantId, e);
            return List.of();
        }
    }

    /**
     * Convert PrePayrollCalculation entity to DTO
     */
    private PrePayrollCalculationDTO convertToDto(PrePayrollCalculation entity) {
        PrePayrollCalculationDTO dto = new PrePayrollCalculationDTO();
        
        // Core identification
        dto.setCalculationId(entity.getCalculationId());
        dto.setTenantId(entity.getTenantId());
        
        // Debug employee relationship
        try {
            PlanParticipant employee = entity.getEmployee();
            if (employee != null) {
                String individualId = employee.getIndividualId();
                dto.setEmployeeId(individualId);
                log.info("ConvertToDto: calculationId={}, employee={}, individualId={}", 
                         entity.getCalculationId(), employee, individualId);
            } else {
                log.warn("ConvertToDto: calculationId={}, employee is null", entity.getCalculationId());
                dto.setEmployeeId(null);
            }
        } catch (Exception e) {
            log.error("ConvertToDto: Failed to get employee for calculationId={}: {}", 
                     entity.getCalculationId(), e.getMessage(), e);
            dto.setEmployeeId(null);
        }
        
        dto.setPayrollPeriodStart(entity.getPayrollPeriodStart());
        dto.setPayrollPeriodEnd(entity.getPayrollPeriodEnd());
        dto.setCalculationDate(entity.getCalculationDate());
        
        // Calculation amounts
        dto.setEmployeeContributionAmount(entity.getEmployeeContributionAmount());
        dto.setEmployerMatchAmount(entity.getEmployerMatchAmount());
        dto.setProfitSharingAmount(entity.getProfitSharingAmount());
        dto.setTotalContributionAmount(entity.getTotalContributionAmount());
        
        // Status fields
        dto.setStatus(entity.getStatus().name());
        dto.setErrorMessage(entity.getErrorMessage());
        
        return dto;
    }

    @Override
    public PrePayrollCalculationDTO read() throws Exception {
        try {
            if (!initialized) {
                log.error("FinchDeductionBatchReader not initialized. Call beforeStep() first.");
                throw new IllegalStateException("FinchDeductionBatchReader not initialized. Call beforeStep() first.");
            }
            
            if (delegateReader == null) {
                log.debug("DelegateReader is null, returning null");
                return null;
            }
            
            PrePayrollCalculationDTO calculationResult = delegateReader.read();
            
            if (calculationResult != null) {
                processedCount.incrementAndGet();
                log.debug("Reading calculation result: {} for Finch deduction ({}/{} processed)", 
                         calculationResult.getCalculationId(), processedCount.get(), totalCount.get());
                
                // Force Spring Batch to recognize this as a read operation
                // This helps with proper counting in the monitoring
                if (processedCount.get() % BatchConstants.DEDUCTION_CHUNK_SIZE == 0) {
                    log.info("Completed deduction chunk: {}/{} calculation results processed", processedCount.get(), totalCount.get());
                }
                
                // Ensure Spring Batch recognizes this read operation
                return calculationResult;
            } else {
                log.info("No more calculation results to read for Finch deduction. Total processed: {}/{}", processedCount.get(), totalCount.get());
                return null;
            }
            
        } catch (Exception e) {
            log.error("Error reading calculation result data for Finch deduction in tenant: {}", tenantId, e);
            log.error("Exception details: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    /**
     * Get the total count of calculation results to be processed
     */
    public int getTotalCalculationResultCount() {
        if (delegateReader == null) {
            return 0;
        }
        
        try {
            List<PrePayrollCalculation> allCalculations = prePayrollCalculationRepository.findByTenantIdAndStatus(
                tenantId, com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus.SUCCESS);
            return allCalculations.size();
        } catch (Exception e) {
            log.warn("Failed to get total calculation result count for tenant: {}", tenantId, e);
            return 0;
        }
    }

    /**
     * Check if the reader has been properly initialized
     */
    public boolean isInitialized() {
        return initialized && tenantId != null && delegateReader != null;
    }

    /**
     * Get the current tenant ID being processed
     */
    public String getTenantId() {
        return tenantId;
    }
}
