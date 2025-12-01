package com.glidingpath.rules.contributions.writer;

import com.glidingpath.common.dto.PrePayrollCalculationDTO;
import com.glidingpath.core.entity.PrePayrollCalculation;
import com.glidingpath.core.repository.PrePayrollCalculationRepository;
import com.glidingpath.rules.contributions.processor.FinchDeductionBatchProcessor;

import constants.BatchConstants;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinchDeductionBatchWriter implements ItemWriter<PrePayrollCalculationDTO> {

    private final PrePayrollCalculationRepository prePayrollCalculationRepository;
    
    // Removed @Autowired FinchDeductionBatchProcessor to fix circular dependency
    
    private String tenantId;
    private StepExecution stepExecution;
    private List<PrePayrollCalculationDTO> allDeductionResults = new ArrayList<>();
    
    // Statistics tracking
    private int totalProcessed = 0;
    private int createdCount = 0;
    private int failedCount = 0;
    private int skippedCount = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        this.allDeductionResults.clear();
        this.totalProcessed = 0;
        this.createdCount = 0;
        this.failedCount = 0;
        this.skippedCount = 0;
        
        log.info("Initializing FinchDeductionBatchWriter for tenant: {}", tenantId);
    }

    @Override
    public void write(Chunk<? extends PrePayrollCalculationDTO> chunk) throws Exception {
        List<? extends PrePayrollCalculationDTO> deductionResults = chunk.getItems();
        
        if (deductionResults == null || deductionResults.isEmpty()) {
            return;
        }

        log.debug("Writing {} deduction results for tenant: {}", deductionResults.size(), tenantId);
        
        // Collect all results for job execution context
        allDeductionResults.addAll(deductionResults);
        
        // Update statistics
        for (PrePayrollCalculationDTO result : deductionResults) {
            totalProcessed++;
            
            String finchStatus = result.getFinchStatus();
            if ("CREATED".equals(finchStatus)) {
                createdCount++;
            } else if ("FAILED".equals(finchStatus)) {
                failedCount++;
            } else if ("SKIPPED".equals(finchStatus)) {
                skippedCount++;
            }
        }
        
        // Update calculation entities with Finch deduction status
        updateCalculationEntities(deductionResults);
        
        // Force Spring Batch to recognize this as a write operation
        // This helps with proper counting in the monitoring
        if (totalProcessed % BatchConstants.DEDUCTION_CHUNK_SIZE == 0) {
            log.info("Completed Finch deduction writing chunk: {}/{} results written (Created: {}, Failed: {}, Skipped: {})", 
                    totalProcessed, "batch", createdCount, failedCount, skippedCount);
        }
        
        log.debug("Successfully processed {} deduction results. Created: {}, Failed: {}, Skipped: {}", 
                deductionResults.size(), createdCount, failedCount, skippedCount);
    }

    /**
     * Update calculation entities with Finch deduction status
     */
    private void updateCalculationEntities(List<? extends PrePayrollCalculationDTO> deductionResults) {
        try {
            // Get employee IDs for batch update
            List<String> employeeIds = deductionResults.stream()
                .map(PrePayrollCalculationDTO::getEmployeeId)
                .collect(Collectors.toList());
            
            // Fetch calculation entities using the correct repository method
            List<PrePayrollCalculation> calculations = new ArrayList<>();
            for (String employeeId : employeeIds) {
                List<PrePayrollCalculation> employeeCalculations = prePayrollCalculationRepository
                    .findByTenantIdAndEmployee_IndividualIdOrderByUpdatedAtDesc(tenantId, employeeId);
                if (!employeeCalculations.isEmpty()) {
                    calculations.add(employeeCalculations.get(0)); // Get the latest calculation
                }
            }
            
            // Update Finch deduction status
            for (PrePayrollCalculation calculation : calculations) {
                PrePayrollCalculationDTO deductionResult = deductionResults.stream()
                    .filter(result -> result.getEmployeeId().equals(calculation.getEmployee().getIndividualId()))
                    .findFirst()
                    .orElse(null);
                
                if (deductionResult != null) {
                    updateCalculationFinchStatus(calculation, deductionResult);
                }
            }
            
            // Batch save all updated calculations
            prePayrollCalculationRepository.saveAll(calculations);
            
            log.debug("Updated Finch deduction status for {} calculations in database", calculations.size());
            
        } catch (Exception e) {
            log.error("Failed to update calculation entities with Finch deduction status for tenant: {}", tenantId, e);
            // Don't throw exception - allow batch to continue
        }
    }

    /**
     * Update individual calculation entity with Finch deduction status
     */
    private void updateCalculationFinchStatus(PrePayrollCalculation calculation, PrePayrollCalculationDTO deductionResult) {
        // Update status to reflect Finch processing
        if ("CREATED".equals(deductionResult.getFinchStatus())) {
            calculation.setStatus(com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus.SUCCESS);
        } else if ("FAILED".equals(deductionResult.getFinchStatus())) {
            calculation.setStatus(com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus.FAILED);
            calculation.setErrorMessage(deductionResult.getFinchErrorMessage());
        }
        
        // Update audit fields
        calculation.setUpdatedAt(LocalDateTime.now());
        calculation.setUpdatedBy("Spring Batch Finch Deduction Job");
        
        // Add processing timestamp
        if ("CREATED".equals(deductionResult.getFinchStatus())) {
            calculation.setProcessedAt(LocalDateTime.now());
        }
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        // Store only serializable data in job execution context
        // Convert to a simple list of deduction IDs and status
        List<String> successfulDeductionIds = allDeductionResults.stream()
            .filter(result -> "CREATED".equals(result.getFinchStatus()))
            .map(PrePayrollCalculationDTO::getCalculationId)
            .collect(Collectors.toList());
        
        stepExecution.getJobExecution().getExecutionContext()
            .put("successfulDeductionIds", successfulDeductionIds);
        
        // Store statistics in step execution context
        stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_TOTAL_PROCESSED, totalProcessed);
        stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_SUCCESS_COUNT, createdCount);
        stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_FAILURE_COUNT, failedCount);
        
        // Log final statistics
        log.info("Finch Deduction Batch Processing Complete for tenant: {}", tenantId);
        log.info("Final Statistics:");
        log.info("   - Total Processed: {}", totalProcessed);
        log.info("   - Deductions Created: {}", createdCount);
        log.info("   - Failed: {}", failedCount);
        log.info("   - Skipped: {}", skippedCount);
        log.info("   - Success Rate: {:.1f}%", 
                totalProcessed > 0 ? (double) createdCount / totalProcessed * 100 : 0);
        
        // Store deduction results count for reporting
        stepExecution.getJobExecution().getExecutionContext()
            .put("deductionsCreatedCount", createdCount);
        
        // Store total deductions count for reporting
        stepExecution.getJobExecution().getExecutionContext()
            .put("totalDeductionsCount", totalProcessed);
    }

    /**
     * Get all deduction results processed in this batch
     */
    public List<PrePayrollCalculationDTO> getAllDeductionResults() {
        return new ArrayList<>(allDeductionResults);
    }

    /**
     * Get successfully created deductions only
     */
    public List<PrePayrollCalculationDTO> getCreatedDeductions() {
        return allDeductionResults.stream()
            .filter(result -> "CREATED".equals(result.getFinchStatus()))
            .collect(Collectors.toList());
    }

    /**
     * Get processing statistics
     */
    public DeductionStats getDeductionStats() {
        return new DeductionStats(totalProcessed, createdCount, failedCount, skippedCount);
    }

    /**
     * Statistics class for deduction processing
     */
    public static class DeductionStats {
        private final int totalProcessed;
        private final int createdCount;
        private final int failedCount;
        private final int skippedCount;

        public DeductionStats(int totalProcessed, int createdCount, int failedCount, int skippedCount) {
            this.totalProcessed = totalProcessed;
            this.createdCount = createdCount;
            this.failedCount = failedCount;
            this.skippedCount = skippedCount;
        }

        public int getTotalProcessed() { return totalProcessed; }
        public int getCreatedCount() { return createdCount; }
        public int getFailedCount() { return failedCount; }
        public int getSkippedCount() { return skippedCount; }
        public double getSuccessRate() { 
            return totalProcessed > 0 ? (double) createdCount / totalProcessed * 100 : 0; 
        }
    }
}
