package com.glidingpath.rules.contributions.writer;

import com.glidingpath.common.dto.EmployeeEligibilityDTO;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.repository.PlanParticipantRepository;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EligibilityBatchWriter implements ItemWriter<EmployeeEligibilityDTO> {

    private final PlanParticipantRepository planParticipantRepository;
    
    // Removed @Autowired EligibilityBatchProcessor to fix circular dependency
    
    private String tenantId;
    private StepExecution stepExecution;
    private List<String> eligibleEmployeeIds = new ArrayList<>(); // Store only IDs to avoid serialization issues
    
    // Statistics tracking
    private int totalProcessed = 0;
    private int eligibleCount = 0;
    private int ineligibleCount = 0;
    private int errorCount = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        this.eligibleEmployeeIds.clear();
        this.totalProcessed = 0;
        this.eligibleCount = 0;
        this.ineligibleCount = 0;
        this.errorCount = 0;
        
        log.info("Initializing EligibilityBatchWriter for tenant: {}", tenantId);
    }

    @Override
    public void write(Chunk<? extends EmployeeEligibilityDTO> chunk) throws Exception {
        List<? extends EmployeeEligibilityDTO> eligibilityResults = chunk.getItems();
        
        if (eligibilityResults == null || eligibilityResults.isEmpty()) {
            log.debug("Empty chunk received in EligibilityBatchWriter.write()");
            return;
        }

        log.debug("Writing {} eligibility results for tenant: {}", eligibilityResults.size(), tenantId);
        
        try {
            // Update statistics and collect eligible employee IDs
            for (EmployeeEligibilityDTO result : eligibilityResults) {
                totalProcessed++;
                
                if (result.getEligibilityReason() != null && result.getEligibilityReason().startsWith("Error")) {
                    errorCount++;
                } else if (result.isEligible()) {
                    eligibleCount++;
                    // Only store eligible employee IDs to avoid serialization issues
                    if (result.getEmployeeId() != null) {
                        eligibleEmployeeIds.add(result.getEmployeeId());
                    }
                } else {
                    ineligibleCount++;
                }
            }
            
            // Only log statistics every 50 processed results to reduce overhead
            if (totalProcessed % 50 == 0) {
                log.debug("Statistics updated - Total: {}, Eligible: {}, Ineligible: {}, Errors: {}", 
                        totalProcessed, eligibleCount, ineligibleCount, errorCount);
            }
            
            // Update eligibility information in PlanParticipant entities
            // Update on every chunk to ensure all results are processed
            log.debug("Updating PlanParticipant entities in database...");
            updatePlanParticipantEligibility(eligibilityResults);
            log.debug("PlanParticipant entities updated successfully");
            
            // Calculate current chunk statistics for accurate logging
            int chunkEligible = 0;
            int chunkIneligible = 0;
            int chunkErrors = 0;
            
            for (EmployeeEligibilityDTO result : eligibilityResults) {
                if (result.getEligibilityReason() != null && result.getEligibilityReason().startsWith("Error")) {
                    chunkErrors++;
                } else if (result.isEligible()) {
                    chunkEligible++;
                } else {
                    chunkIneligible++;
                }
            }
            
            log.debug("Successfully processed {} eligibility results. Current chunk - Eligible: {}, Ineligible: {}, Errors: {}", 
                    eligibilityResults.size(), chunkEligible, chunkIneligible, chunkErrors);
                    
        } catch (Exception e) {
            log.error("Failed to write eligibility results in EligibilityBatchWriter.write()", e);
            // Don't throw exception - allow batch to continue
        }
    }

    /**
     * Update eligibility information in PlanParticipant entities with improved batch processing
     */
    @Transactional
    private void updatePlanParticipantEligibility(List<? extends EmployeeEligibilityDTO> eligibilityResults) {
        try {
            if (eligibilityResults.isEmpty()) {
                return;
            }
            
            // Get employee IDs for batch update
            List<String> employeeIds = eligibilityResults.stream()
                .map(EmployeeEligibilityDTO::getEmployeeId)
                .filter(id -> id != null) // Filter out null IDs
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
            
            if (employeeIds.isEmpty()) {
                log.warn("No valid employee IDs found in eligibility results");
                return;
            }
            
            // Fetch PlanParticipant entities in smaller batches to avoid memory issues
            int batchSize = 50; // Use larger batch size for database operations
            List<PlanParticipant> allEmployees = new ArrayList<>();
            
            for (int i = 0; i < employeeIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, employeeIds.size());
                List<String> batchIds = employeeIds.subList(i, endIndex);
                
                List<PlanParticipant> batchEmployees = planParticipantRepository.findByIndividualIdIn(batchIds.stream().collect(Collectors.toSet()));
                allEmployees.addAll(batchEmployees);
            }
            
            // Update eligibility information efficiently
            for (PlanParticipant employee : allEmployees) {
                EmployeeEligibilityDTO eligibilityResult = eligibilityResults.stream()
                    .filter(result -> result.getEmployeeId() != null && result.getEmployeeId().equals(employee.getIndividualId()))
                    .findFirst()
                    .orElse(null);
                
                if (eligibilityResult != null) {
                    updateEmployeeEligibility(employee, eligibilityResult);
                }
            }
            
            // Save in smaller batches to avoid transaction timeouts
            if (!allEmployees.isEmpty()) {
                for (int i = 0; i < allEmployees.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, allEmployees.size());
                    List<PlanParticipant> batchToSave = allEmployees.subList(i, endIndex);
                    planParticipantRepository.saveAll(batchToSave);
                }
                
                log.debug("Updated eligibility information for {} employees in database", allEmployees.size());
            }
            
        } catch (Exception e) {
            log.error("Failed to update eligibility information in database for tenant: {}", tenantId, e);
            // Don't throw exception - allow batch to continue
        }
    }

    /**
     * Update individual employee eligibility information
     */
    private void updateEmployeeEligibility(PlanParticipant employee, EmployeeEligibilityDTO eligibilityResult) {
        LocalDate today = LocalDate.now();
        
        // Update core eligibility fields
        employee.setIsEligibleFor401k(eligibilityResult.isEligible());
        employee.setEligibilityDate(eligibilityResult.getEligibilityDate());
        employee.setLastEligibilityCheck(today);
        employee.setEligibilityReason(eligibilityResult.getEligibilityReason());
        
        // Set eligibility status and next check date
        if (eligibilityResult.isEligible()) {
            employee.setEligibilityStatus("ELIGIBLE");
            employee.setNextEligibilityCheckDate(today.plusMonths(6)); // Check again in 6 months
        } else {
            employee.setEligibilityStatus("NOT_ELIGIBLE");
            
            // Set next check based on reason
            String reason = eligibilityResult.getEligibilityReason();
            if (reason != null) {
                if (reason.contains("age")) {
                    employee.setNextEligibilityCheckDate(today.plusYears(1)); // Check annually for age
                } else if (reason.contains("service")) {
                    employee.setNextEligibilityCheckDate(today.plusMonths(3)); // Check quarterly for service
                } else {
                    employee.setNextEligibilityCheckDate(today.plusMonths(1)); // Default monthly check
                }
            } else {
                employee.setNextEligibilityCheckDate(today.plusMonths(1));
            }
        }
        
        // Set audit information
        employee.setEligibilityNotes("Processed by Spring Batch eligibility job on " + today);
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        try {
            // Store only simple, serializable data
            stepExecution.getJobExecution().getExecutionContext()
                .put("eligibleEmployeeIds", new ArrayList<>(eligibleEmployeeIds));
            
            // Store statistics in step execution context
            stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_TOTAL_PROCESSED, totalProcessed);
            stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_SUCCESS_COUNT, eligibleCount + ineligibleCount);
            stepExecution.getExecutionContext().put(BatchConstants.CONTEXT_FAILURE_COUNT, errorCount);
            
            // Log final statistics
            log.info("Eligibility Batch Processing Complete for tenant: {}", tenantId);
            log.info("Final Statistics:");
            log.info("   - Total Processed: {}", totalProcessed);
            log.info("   - Eligible: {}", eligibleCount);
            log.info("   - Not Eligible: {}", ineligibleCount);
            log.info("   - Errors: {}", errorCount);
            log.info("   - Success Rate: {:.1f}%", 
                    totalProcessed > 0 ? (double)(eligibleCount + ineligibleCount) / totalProcessed * 100 : 0);
            
            // Store eligible employees count for next step
            stepExecution.getJobExecution().getExecutionContext()
                .put("eligibleEmployeesCount", eligibleCount);
            
            // Store total employees count for reporting
            stepExecution.getJobExecution().getExecutionContext()
                .put("totalEmployeesCount", totalProcessed);
                
        } catch (Exception e) {
            log.error("Error in afterStep for EligibilityBatchWriter", e);
            // Don't throw exception to avoid breaking the batch job
        }
    }

    /**
     * Get eligible employee IDs processed in this batch
     */
    public List<String> getEligibleEmployeeIds() {
        return new ArrayList<>(eligibleEmployeeIds);
    }

    /**
     * Get processing statistics
     */
    public EligibilityStats getEligibilityStats() {
        return new EligibilityStats(totalProcessed, eligibleCount, ineligibleCount, errorCount);
    }

    /**
     * Statistics class for eligibility processing
     */
    public static class EligibilityStats {
        private final int totalProcessed;
        private final int eligibleCount;
        private final int ineligibleCount;
        private final int errorCount;

        public EligibilityStats(int totalProcessed, int eligibleCount, int ineligibleCount, int errorCount) {
            this.totalProcessed = totalProcessed;
            this.eligibleCount = eligibleCount;
            this.ineligibleCount = ineligibleCount;
            this.errorCount = errorCount;
        }

        public int getTotalProcessed() { return totalProcessed; }
        public int getEligibleCount() { return eligibleCount; }
        public int getIneligibleCount() { return ineligibleCount; }
        public int getErrorCount() { return errorCount; }
        public double getEligibilityRate() { 
            return totalProcessed > 0 ? (double) eligibleCount / totalProcessed * 100 : 0; 
        }
        public double getSuccessRate() { 
            return totalProcessed > 0 ? (double)(eligibleCount + ineligibleCount) / totalProcessed * 100 : 0; 
        }
    }
}
