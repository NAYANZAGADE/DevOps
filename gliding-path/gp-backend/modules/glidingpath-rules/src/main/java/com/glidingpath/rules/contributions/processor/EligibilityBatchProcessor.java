package com.glidingpath.rules.contributions.processor;

import com.glidingpath.common.dto.EmployeeEligibilityDTO;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.rules.util.EligibilityProcessingUtility;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EligibilityBatchProcessor implements ItemProcessor<PlanParticipant, EmployeeEligibilityDTO> {

    private final EligibilityProcessingUtility eligibilityUtility;
    
    private String tenantId;
    private int processedCount = 0;
    private int successCount = 0;
    private int failureCount = 0;
    private long startTime;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        this.processedCount = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.startTime = System.currentTimeMillis();
        
        log.info("Initializing EligibilityBatchProcessor for tenant: {}", tenantId);
        
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is required for eligibility batch processing");
        }
    }

    @Override
    public EmployeeEligibilityDTO process(PlanParticipant employee) throws Exception {
        if (employee == null) {
            log.warn("Received null employee in EligibilityBatchProcessor.process()");
            return null;
        }

        processedCount++;
        
        try {
          
            if (processedCount % 5 == 0) {
                log.debug("Processing employee: {} ({}/batch)", 
                        employee.getIndividualId(), processedCount);
            }
            
            // Validate employee data
            if (employee.getIndividualId() == null) {
                log.error("Employee has null individualId, skipping");
                return eligibilityUtility.createFailedEligibilityResult(employee, 
                    new IllegalArgumentException("Employee individualId is null"), tenantId);
            }
            
            // Convert PlanParticipant to EmployeeEligibilityDTO using shared utility
            EmployeeEligibilityDTO eligibilityDto = eligibilityUtility.convertToEligibilityDto(employee);
            
            // Evaluate eligibility using shared utility
            EmployeeEligibilityDTO result = eligibilityUtility.evaluateEligibility(eligibilityDto, tenantId);
            
            successCount++;
            
            // Only log detailed eligibility results every 10 employees
            if (processedCount % 10 == 0) {
                if (result.isEligible()) {
                    log.debug("Employee {} is ELIGIBLE", employee.getIndividualId());
                } else {
                    log.debug("Employee {} is NOT ELIGIBLE: {}", 
                            employee.getIndividualId(), result.getEligibilityReason());
                }
            }
            
            // Log progress every 20 employees instead of every chunk
            if (processedCount % 20 == 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                double avgTimePerEmployee = (double) elapsed / processedCount;
                log.info("Eligibility progress: {} employees processed (Success: {}, Failed: {}) - Avg: {:.2f}ms per employee", 
                        processedCount, successCount, failureCount, avgTimePerEmployee);
            }
            
            return result;
            
        } catch (Exception e) {
            failureCount++;
            log.error("Failed to process eligibility for employee: {} in tenant: {}", 
                    employee.getIndividualId(), tenantId, e);
            
            // Create failed eligibility result instead of throwing exception
            // This allows the batch to continue processing other employees
            return eligibilityUtility.createFailedEligibilityResult(employee, e, tenantId);
        }
    }

    /**
     * Get processing statistics
     */
    public ProcessingStats getProcessingStats() {
        long elapsed = System.currentTimeMillis() - startTime;
        return new ProcessingStats(processedCount, successCount, failureCount, elapsed);
    }

    /**
     * Reset statistics for new batch processing
     */
    public void resetStats() {
        this.processedCount = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Statistics class for tracking processing results
     */
    public static class ProcessingStats {
        private final int totalProcessed;
        private final int successful;
        private final int failed;
        private final long elapsedTimeMs;

        public ProcessingStats(int totalProcessed, int successful, int failed, long elapsedTimeMs) {
            this.totalProcessed = totalProcessed;
            this.successful = successful;
            this.failed = failed;
            this.elapsedTimeMs = elapsedTimeMs;
        }

        public int getTotalProcessed() { return totalProcessed; }
        public int getSuccessful() { return successful; }
        public int getFailed() { return failed; }
        public long getElapsedTimeMs() { return elapsedTimeMs; }
        public double getSuccessRate() { 
            return totalProcessed > 0 ? (double) successful / totalProcessed * 100 : 0; 
        }
        public double getAverageTimePerEmployee() {
            return totalProcessed > 0 ? (double) elapsedTimeMs / totalProcessed : 0;
        }
    }
}
