package com.glidingpath.rules.contributions.processor;

import com.glidingpath.common.dto.PrePayrollCalculationDTO;
// import com.glidingpath.common.service.FinchBenefitService;
import com.glidingpath.common.service.FinchBenefitService;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinchDeductionBatchProcessor implements ItemProcessor<PrePayrollCalculationDTO, PrePayrollCalculationDTO> {

    private final FinchBenefitService finchBenefitService;
    
    private String tenantId;
    private int processedCount = 0;
    private int successCount = 0;
    private int failureCount = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        this.processedCount = 0;
        this.successCount = 0;
        this.failureCount = 0;
        
        log.info("Initializing FinchDeductionBatchProcessor for tenant: {}", tenantId);
        
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is required for Finch deduction batch processing");
        }
    }

    @Override
    public PrePayrollCalculationDTO process(PrePayrollCalculationDTO calculation) throws Exception {
        if (calculation == null) {
            return null;
        }

        processedCount++;
        
        try {
            log.debug("Processing Finch deduction for employee: {} ({} of batch)", 
                    calculation.getEmployeeId(), processedCount);
            
            // Validate that the calculation is successful
            if (!"SUCCESS".equals(calculation.getStatus())) {
                log.warn("Calculation for employee {} is not successful, skipping Finch deduction", calculation.getEmployeeId());
                failureCount++;
                calculation.setFinchStatus("SKIPPED");
                calculation.setErrorMessage("Skipped: Calculation not successful");
                return calculation;
            }
            
            // Create Finch deductions using the service
            Map<String, String> deductionResult = finchBenefitService.createDeduction(tenantId, 
                Map.of("employeeId", calculation.getEmployeeId(), "amount", calculation.getTotalContributionAmount()));
            
            // Update the calculation with Finch status
            if (deductionResult.containsKey("success") && "true".equals(deductionResult.get("success"))) {
                calculation.setFinchStatus("CREATED");
            } else {
                calculation.setFinchStatus("FAILED");
                calculation.setFinchErrorMessage(deductionResult.get("error") != null ? deductionResult.get("error") : "Unknown error");
            }
            
            successCount++;
            
            log.debug("Successfully created Finch deduction for employee: {} - Status: {}", 
                    calculation.getEmployeeId(), calculation.getFinchStatus());
            
            // Force Spring Batch to recognize this as a processing operation
            // This helps with proper counting in the monitoring
            if (processedCount % BatchConstants.DEDUCTION_CHUNK_SIZE == 0) {
                log.info("Completed Finch deduction processing chunk: {}/{} calculations processed (Success: {}, Failed: {})", 
                        processedCount, "batch", successCount, failureCount);
            }
            
            return calculation;
            
        } catch (Exception e) {
            failureCount++;
            log.error("Failed to create Finch deduction for employee: {} in tenant: {}", 
                    calculation.getEmployeeId(), tenantId, e);
            
            // Update calculation with failure status
            calculation.setFinchStatus("FAILED");
            calculation.setErrorMessage("Finch deduction failed: " + e.getMessage());
            
            return calculation;
        }
    }

    /**
     * Get processing statistics
     */
    public ProcessingStats getProcessingStats() {
        return new ProcessingStats(processedCount, successCount, failureCount);
    }

    /**
     * Statistics class for tracking processing results
     */
    public static class ProcessingStats {
        private final int totalProcessed;
        private final int successful;
        private final int failed;

        public ProcessingStats(int totalProcessed, int successful, int failed) {
            this.totalProcessed = totalProcessed;
            this.successful = successful;
            this.failed = failed;
        }

        public int getTotalProcessed() { return totalProcessed; }
        public int getSuccessful() { return successful; }
        public int getFailed() { return failed; }
        public double getSuccessRate() { 
            return totalProcessed > 0 ? (double) successful / totalProcessed * 100 : 0; 
        }
    }
}
