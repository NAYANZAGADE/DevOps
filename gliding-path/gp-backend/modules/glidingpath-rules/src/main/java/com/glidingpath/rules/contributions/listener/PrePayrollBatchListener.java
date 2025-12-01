package com.glidingpath.rules.contributions.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Spring Batch Job and Step Listener for Pre-Payroll Processing
 * 
 * Provides comprehensive monitoring of both job and step execution
 * to ensure proper read/write counting and progress tracking.
 */
@Slf4j
@Component
public class PrePayrollBatchListener implements JobExecutionListener {

    private LocalDateTime jobStartTime;

    // ========================================
    // JOB EXECUTION LISTENER METHODS
    // ========================================
    
    @BeforeJob
    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobStartTime = LocalDateTime.now();
        String jobName = jobExecution.getJobInstance().getJobName();
        String tenantId = jobExecution.getJobParameters().getString("tenantId");
        
        log.info("Starting Spring Batch Job: {} for tenant: {} at {}", 
                jobName, tenantId, jobStartTime);
        log.info("Job Parameters: {}", jobExecution.getJobParameters());
    }

    @AfterJob
    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime jobEndTime = LocalDateTime.now();
        String jobName = jobExecution.getJobInstance().getJobName();
        String tenantId = jobExecution.getJobParameters().getString("tenantId");
        
        // Calculate duration
        long durationMs = java.time.Duration.between(jobStartTime, jobEndTime).toMillis();
        
        // Log job completion summary
        log.info("Completed Spring Batch Job: {} for tenant: {} at {}", 
                jobName, tenantId, jobEndTime);
        log.info("Job Duration: {}ms", durationMs);
        log.info("Job Exit Status: {}", jobExecution.getExitStatus());
        
        // Log any failures
        if (jobExecution.getFailureExceptions() != null && !jobExecution.getFailureExceptions().isEmpty()) {
            log.warn("Job completed with {} failure exceptions:", jobExecution.getFailureExceptions().size());
            jobExecution.getFailureExceptions().forEach(exception -> 
                log.warn("   - {}", exception.getMessage())
            );
        }
        
        // Log step summaries (Spring Batch provides this automatically)
        if (jobExecution.getStepExecutions() != null && !jobExecution.getStepExecutions().isEmpty()) {
            log.info("Step Execution Summary:");
            jobExecution.getStepExecutions().forEach(step -> {
                log.info("   - {}: Read={}, Written={}, Skipped={}", 
                    step.getStepName(),
                    step.getReadCount(),
                    step.getWriteCount(),
                    step.getSkipCount()
                );
            });
        }
    }


}
