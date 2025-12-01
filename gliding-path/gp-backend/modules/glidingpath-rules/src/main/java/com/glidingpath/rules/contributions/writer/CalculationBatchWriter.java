package com.glidingpath.rules.contributions.writer;

import com.glidingpath.common.dto.PrePayrollCalculationDTO;
import com.glidingpath.core.entity.PrePayrollCalculation;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.repository.PrePayrollCalculationRepository;
import com.glidingpath.rules.contributions.processor.CalculationBatchProcessor;

import constants.BatchConstants;

import com.glidingpath.core.repository.PlanParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.glidingpath.core.entity.PlanParticipant;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalculationBatchWriter implements ItemWriter<PrePayrollCalculationDTO> {

    private final PrePayrollCalculationRepository prePayrollCalculationRepository;
    private final PlanParticipantRepository planParticipantRepository;
    

    
    private String tenantId;
    private LocalDateTime payrollPeriodStart;
    private LocalDateTime payrollPeriodEnd;
    private StepExecution stepExecution;
    private List<PrePayrollCalculationDTO> allCalculationResults = new ArrayList<>();
    
    // Statistics tracking
    private int totalProcessed = 0;
    private int successfulCount = 0;
    private int failedCount = 0;
    private int errorCount = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        this.payrollPeriodStart = LocalDateTime.parse(
            stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_PAYROLL_PERIOD_START) + "T00:00:00"
        );
        this.payrollPeriodEnd = LocalDateTime.parse(
            stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_PAYROLL_PERIOD_END) + "T23:59:59"
        );
        this.allCalculationResults.clear();
        this.totalProcessed = 0;
        this.successfulCount = 0;
        this.failedCount = 0;
        this.errorCount = 0;
        
        log.info("Initializing CalculationBatchWriter for tenant: {} period: {} to {}", 
                tenantId, payrollPeriodStart, payrollPeriodEnd);
    }

    @Override
    public void write(Chunk<? extends PrePayrollCalculationDTO> chunk) throws Exception {
        List<? extends PrePayrollCalculationDTO> calculationResults = chunk.getItems();
        
        if (calculationResults == null || calculationResults.isEmpty()) {
            return;
        }

        log.debug("Writing {} calculation results for tenant: {}", calculationResults.size(), tenantId);
        
        // Collect all results for job execution context
        allCalculationResults.addAll(calculationResults);
        
        // Update statistics
        for (PrePayrollCalculationDTO result : calculationResults) {
            totalProcessed++;
            
            if ("FAILED".equals(result.getStatus())) {
                failedCount++;
            } else if ("SUCCESS".equals(result.getStatus())) {
                successfulCount++;
            } else {
                errorCount++;
            }
        }
        
        // Convert DTOs to entities and save to database
        saveCalculationResults(calculationResults);
        
        // Force Spring Batch to recognize this as a write operation
        // This helps with proper counting in the monitoring
        if (totalProcessed % BatchConstants.CALCULATION_CHUNK_SIZE == 0) {
            log.info("Completed calculation writing chunk: {}/{} results written (Successful: {}, Failed: {}, Errors: {})", 
                    totalProcessed, "batch", successfulCount, failedCount, errorCount);
        }
        
        log.debug("Successfully processed {} calculation results. Successful: {}, Failed: {}, Errors: {}", 
                calculationResults.size(), successfulCount, failedCount, errorCount);
    }

    /**
     * Save calculation results to database with proper employee relationship handling
     */
    private void saveCalculationResults(List<? extends PrePayrollCalculationDTO> calculationResults) {
        try {
            // Convert DTOs to entities
            List<PrePayrollCalculation> entities = calculationResults.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
            
            // Validate entities before saving
            List<PrePayrollCalculation> validEntities = entities.stream()
                .filter(this::validateEntity)
                .collect(Collectors.toList());
            
            if (validEntities.size() != entities.size()) {
                log.warn("Filtered out {} invalid entities out of {} total for tenant: {}", 
                        entities.size() - validEntities.size(), entities.size(), tenantId);
            }
            
            // Batch save all valid entities
            if (!validEntities.isEmpty()) {
                List<PrePayrollCalculation> savedEntities = prePayrollCalculationRepository.saveAll(validEntities);
                
                log.info("Successfully saved {} calculation entities to database for tenant: {}",
                        savedEntities.size(), tenantId);
                
                // Log sample of saved calculations
                savedEntities.stream().limit(3).forEach(entity -> 
                    log.debug("Saved calculation: {} for employee: {} with status: {}", 
                            entity.getCalculationId(), 
                            entity.getEmployee() != null ? entity.getEmployee().getIndividualId() : "UNKNOWN",
                            entity.getStatus())
                );
            } else {
                log.warn("No valid entities to save for tenant: {}", tenantId);
            }
            
        } catch (Exception e) {
            log.error("Failed to save calculation results to database for tenant: {}", tenantId, e);
            throw e;
        }
    }
    
    /**
     * Validate entity before saving to prevent database errors
     */
    private boolean validateEntity(PrePayrollCalculation entity) {
        // Check required fields
        if (entity.getCalculationId() == null || entity.getCalculationId().trim().isEmpty()) {
            log.warn("Skipping entity with null/empty calculationId");
            return false;
        }
        
        if (entity.getTenantId() == null || entity.getTenantId().trim().isEmpty()) {
            log.warn("Skipping entity with null/empty tenantId for calculation: {}", entity.getCalculationId());
            return false;
        }
        
        if (entity.getEmployee() == null || entity.getEmployee().getIndividualId() == null) {
            log.warn("Skipping entity with null employee relationship for calculation: {}", entity.getCalculationId());
            return false;
        }
        
        if (entity.getPayrollPeriodStart() == null || entity.getPayrollPeriodEnd() == null) {
            log.warn("Skipping entity with null payroll period for calculation: {}", entity.getCalculationId());
            return false;
        }
        
        return true;
    }

    /**
     * Convert DTO to entity with proper field mapping
     */
    private PrePayrollCalculation convertToEntity(PrePayrollCalculationDTO dto) {
        PrePayrollCalculation entity = new PrePayrollCalculation();
        
        // Core identification
        entity.setCalculationId(dto.getCalculationId());
        entity.setTenantId(dto.getTenantId());
        entity.setPayrollPeriodStart(dto.getPayrollPeriodStart());
        entity.setPayrollPeriodEnd(dto.getPayrollPeriodEnd());
        entity.setCalculationDate(dto.getCalculationDate() != null ? dto.getCalculationDate() : LocalDateTime.now());
        
        // Employee relationship - CRITICAL for database integrity
        if (dto.getEmployeeId() != null) {
            // Fetch the actual PlanParticipant entity from database to avoid transient reference
            try {
                Optional<PlanParticipant> employeeOpt = planParticipantRepository.findByIndividualId(dto.getEmployeeId());
                if (employeeOpt.isPresent()) {
                    entity.setEmployee(employeeOpt.get());
                } else {
                    log.warn("Employee not found for ID: {} when creating calculation entity", dto.getEmployeeId());
                    // Create a minimal reference but mark it as managed
                    PlanParticipant employee = new PlanParticipant();
                    employee.setIndividualId(dto.getEmployeeId());
                    employee.setId(UUID.randomUUID()); // Set a temporary ID
                    entity.setEmployee(employee);
                }
            } catch (Exception e) {
                log.error("Failed to fetch employee for calculation: {}", dto.getEmployeeId(), e);
                // Create a minimal reference as fallback
                PlanParticipant employee = new PlanParticipant();
                employee.setIndividualId(dto.getEmployeeId());
                employee.setId(UUID.randomUUID()); // Set a temporary ID
                entity.setEmployee(employee);
            }
        }
        
        // Calculation amounts - with null safety and default values
        entity.setEmployeeContributionAmount(
            dto.getEmployeeContributionAmount() != null ? dto.getEmployeeContributionAmount() : BigDecimal.ZERO
        );
        entity.setEmployeeContributionPercentage(
            dto.getEmployeeContributionPercentage() != null ? dto.getEmployeeContributionPercentage() : BigDecimal.ZERO
        );
        
        entity.setEmployerMatchAmount(
            dto.getEmployerMatchAmount() != null ? dto.getEmployerMatchAmount() : BigDecimal.ZERO
        );
        entity.setEmployerMatchPercentage(
            dto.getEmployerMatchPercentage() != null ? dto.getEmployerMatchPercentage() : BigDecimal.ZERO
        );
        
        entity.setProfitSharingAmount(
            dto.getProfitSharingAmount() != null ? dto.getProfitSharingAmount() : BigDecimal.ZERO
        );
        entity.setProfitSharingPercentage(
            dto.getProfitSharingPercentage() != null ? dto.getProfitSharingPercentage() : BigDecimal.ZERO
        );
        
        entity.setTotalContributionAmount(
            dto.getTotalContributionAmount() != null ? dto.getTotalContributionAmount() : BigDecimal.ZERO
        );
        entity.setTotalContributionPercentage(
            dto.getTotalContributionPercentage() != null ? dto.getTotalContributionPercentage() : BigDecimal.ZERO
        );
        
        // Base salary and compensation - with null safety
        entity.setBaseSalary(
            dto.getBaseSalary() != null ? dto.getBaseSalary() : BigDecimal.ZERO
        );
        entity.setEligibleCompensation(
            dto.getEligibleCompensation() != null ? dto.getEligibleCompensation() : BigDecimal.ZERO
        );
        
        // Plan configuration references
        entity.setPlanId(dto.getPlanId());
        entity.setEmployerContributionRuleId(dto.getEmployerContributionRuleId());
        entity.setEmployeeContributionConfigId(dto.getEmployeeContributionConfigId());
        entity.setProfitSharingConfigId(dto.getProfitSharingConfigId());
        
        // Status fields - with proper enum mapping
        if (dto.getStatus() != null) {
            try {
                entity.setStatus(com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status '{}' for calculation {}, defaulting to PENDING", dto.getStatus(), dto.getCalculationId());
                entity.setStatus(com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus.PENDING);
            }
        } else {
            entity.setStatus(com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus.PENDING);
        }
        
        entity.setErrorMessage(dto.getErrorMessage());
        
        // Processing metadata
        entity.setProcessedAt(dto.getProcessedAt());
        entity.setReprocessedCount(dto.getReprocessedCount() != null ? dto.getReprocessedCount() : 0);
        entity.setLastReprocessedAt(dto.getLastReprocessedAt());
        
        // Audit fields
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setCreatedBy("Spring Batch Calculation Job");
        entity.setUpdatedBy("Spring Batch Calculation Job");
        
        log.debug("Converted DTO to entity for calculation: {} employee: {}", 
                dto.getCalculationId(), dto.getEmployeeId());
        
        return entity;
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        // Store only serializable data in job execution context for next step
        // Convert to a simple list of calculation IDs and status
        List<String> successfulCalculationIds = allCalculationResults.stream()
            .filter(result -> "SUCCESS".equals(result.getStatus()))
            .map(PrePayrollCalculationDTO::getCalculationId)
            .collect(Collectors.toList());
        
        stepExecution.getJobExecution().getExecutionContext()
            .put("successfulCalculationIds", successfulCalculationIds);
        
        // Store calculation IDs for next step (Finch deduction) to avoid serialization issues
        stepExecution.getJobExecution().getExecutionContext()
            .put(BatchConstants.CONTEXT_CALCULATION_RESULTS, successfulCalculationIds);
        
        // Store statistics in step execution context
        stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_TOTAL_PROCESSED, totalProcessed);
        stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_SUCCESS_COUNT, successfulCount);
        stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_FAILURE_COUNT, failedCount);
        
        // Log final statistics
        log.info("Calculation Batch Processing Complete for tenant: {}", tenantId);
        log.info("Final Statistics:");
        log.info("   - Total Processed: {}", totalProcessed);
        log.info("   - Successful: {}", successfulCount);
        log.info("   - Failed: {}", failedCount);
        log.info("   - Errors: {}", errorCount);
        log.info("   - Success Rate: {:.1f}%", 
                totalProcessed > 0 ? (double) successfulCount / totalProcessed * 100 : 0);
        
        // Store calculation results count for next step
        stepExecution.getJobExecution().getExecutionContext()
            .put("calculationResultsCount", successfulCount);
        
        // Store total calculations count for reporting
        stepExecution.getJobExecution().getExecutionContext()
            .put("totalCalculationsCount", totalProcessed);
    }

    /**
     * Get all calculation results processed in this batch
     */
    public List<PrePayrollCalculationDTO> getAllCalculationResults() {
        return new ArrayList<>(allCalculationResults);
    }

    /**
     * Get successful calculations only
     */
    public List<PrePayrollCalculationDTO> getSuccessfulCalculations() {
        return allCalculationResults.stream()
            .filter(result -> "SUCCESS".equals(result.getStatus()))
            .collect(Collectors.toList());
    }

    /**
     * Get processing statistics
     */
    public CalculationStats getCalculationStats() {
        return new CalculationStats(totalProcessed, successfulCount, failedCount, errorCount);
    }

    /**
     * Statistics class for calculation processing
     */
    public static class CalculationStats {
        private final int totalProcessed;
        private final int successfulCount;
        private final int failedCount;
        private final int errorCount;

        public CalculationStats(int totalProcessed, int successfulCount, int failedCount, int errorCount) {
            this.totalProcessed = totalProcessed;
            this.successfulCount = successfulCount;
            this.failedCount = failedCount;
            this.errorCount = errorCount;
        }

        public int getTotalProcessed() { return totalProcessed; }
        public int getSuccessfulCount() { return successfulCount; }
        public int getFailedCount() { return failedCount; }
        public int getErrorCount() { return errorCount; }
        public double getSuccessRate() { 
            return totalProcessed > 0 ? (double) successfulCount / totalProcessed * 100 : 0; 
        }
    }
}
