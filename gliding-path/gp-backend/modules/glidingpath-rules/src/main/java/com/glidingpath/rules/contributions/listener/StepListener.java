package com.glidingpath.rules.contributions.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("=== STEP STARTING: {} ===", stepExecution.getStepName());
        log.info("Step: {} | Job: {} | Job Execution ID: {}", 
                stepExecution.getStepName(), 
                stepExecution.getJobExecution().getJobInstance().getJobName(),
                stepExecution.getJobExecution().getId());
        log.info("Step Initial State - Read: {}, Written: {}, Skipped: {}, Committed: {}", 
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                stepExecution.getCommitCount());
        log.info("Step Execution Context: {}", stepExecution.getExecutionContext());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("=== STEP COMPLETED: {} ===", stepExecution.getStepName());
        log.info("Step: {} | Exit Status: {}", 
                stepExecution.getStepName(), 
                stepExecution.getExitStatus());
        log.info("Step Final Statistics:");
        log.info("   - Read Count: {}", stepExecution.getReadCount());
        log.info("   - Write Count: {}", stepExecution.getWriteCount());
        log.info("   - Skip Count: {}", stepExecution.getSkipCount());
        log.info("   - Commit Count: {}", stepExecution.getCommitCount());
        log.info("   - Rollback Count: {}", stepExecution.getRollbackCount());
        
        if (stepExecution.getStartTime() != null && stepExecution.getEndTime() != null) {
            long durationMs = java.time.Duration.between(stepExecution.getStartTime(), stepExecution.getEndTime()).toMillis();
            log.info("   - Step Duration: {}ms", durationMs);
        } else {
            log.info("   - Step Duration: Unable to calculate (start or end time is null)");
        }
        
        log.info("Step Execution Context: {}", stepExecution.getExecutionContext());
        return stepExecution.getExitStatus();
    }
}
