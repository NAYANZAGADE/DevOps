package com.glidingpath.rules.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.glidingpath.rules.service.PrePayrollBatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Batch Job Launcher Service with Job Execution Control
 * 
 * This service prevents multiple jobs from running simultaneously for the same tenant.
 * All orchestration, step management, and error handling is handled by Spring Batch itself.
 * 
 * SPRING BATCH-ONLY WORKFLOW:
 * 1. Complete pre-payroll processing: Use this batch service (3 steps: eligibility -> calculation -> deductions)
 * 2. Individual eligibility queries: Use direct database queries for eligibility status
 * 
 * SPRING BATCH-NATIVE APPROACH:
 * - No custom batch logic
 * - No manual step coordination
 * - No custom transaction management
 * - Spring Batch handles everything
 * - PREVENTS MULTIPLE JOBS FROM RUNNING SIMULTANEOUSLY
 * - USES SPRING BATCH-ONLY APPROACH with shared utilities (no duplication)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrePayrollBatchServiceImpl implements PrePayrollBatchService {

    // Spring Batch components - injected by Spring
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    
    // The complete pre-payroll Spring Batch job (3 steps: eligibility -> calculation -> deductions)
    // Uses properly implemented batch components with shared EligibilityProcessingUtility (no duplication)
    @Autowired
    private Job prePayrollJob;

    @Override
    public String processPayrollBatch(String tenantId, 
                                    LocalDate payrollPeriodStart, 
                                    LocalDate payrollPeriodEnd) {
        
        log.info("Launching Spring Batch pre-payroll job for tenant: {} period: {} to {}", 
                tenantId, payrollPeriodStart, payrollPeriodEnd);
        
        try {
            // Check if a job is already running for this tenant
            if (isJobAlreadyRunning(tenantId)) {
                String errorMsg = "A pre-payroll job is already running for tenant: " + tenantId + 
                                ". Please wait for the current job to complete.";
                log.warn(errorMsg);
                return errorMsg;
            }
            
            // Create job parameters for Spring Batch
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("tenantId", tenantId)
                .addString("payrollPeriodStart", payrollPeriodStart.toString())
                .addString("payrollPeriodEnd", payrollPeriodEnd.toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
            
            // Launch the complete Spring Batch job - Spring Batch handles everything else!
            JobExecution jobExecution = jobLauncher.run(prePayrollJob, jobParameters);
            
            log.info("Spring Batch pre-payroll job launched successfully with execution ID: {} for tenant: {}", 
                    jobExecution.getId(), tenantId);
            
            return "Spring Batch job started successfully with ID: " + jobExecution.getId();
            
        } catch (Exception e) {
            log.error("Failed to launch Spring Batch pre-payroll job for tenant: {}", tenantId, e);
            return "Failed to start Spring Batch job: " + e.getMessage();
        }
    }

    /**
     * Check if a pre-payroll job is already running for the given tenant
     */
    private boolean isJobAlreadyRunning(String tenantId) {
        try {
            // Get all job instances for prePayrollJob
            List<JobInstance> jobInstances = jobExplorer.getJobInstances("prePayrollJob", 0, Integer.MAX_VALUE);
            
            for (JobInstance jobInstance : jobInstances) {
                // Get the latest execution for this job instance
                JobExecution latestExecution = jobExplorer.getJobExecutions(jobInstance).stream()
                    .max((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime()))
                    .orElse(null);
                
                if (latestExecution != null) {
                    // Check if this execution is for the same tenant and is still running
                    String executionTenantId = latestExecution.getJobParameters().getString("tenantId");
                    if (tenantId.equals(executionTenantId)) {
                        // Check if the job is actually running (not just STARTED but actively processing)
                        if (latestExecution.getStatus().isRunning() || 
                            latestExecution.getStatus() == org.springframework.batch.core.BatchStatus.STARTING ||
                            latestExecution.getStatus() == org.springframework.batch.core.BatchStatus.STARTED) {
                            
                            // Additional check: if the job has been running for more than 30 minutes, consider it stuck
                            long runningTime = System.currentTimeMillis() - latestExecution.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                            long thirtyMinutes = 30 * 60 * 1000; // 30 minutes in milliseconds
                            
                            if (runningTime > thirtyMinutes) {
                                log.warn("Found stuck pre-payroll job for tenant: {} with execution ID: {} status: {} running for {} minutes", 
                                        tenantId, latestExecution.getId(), latestExecution.getStatus(), runningTime / (60 * 1000));
                                // Don't block new jobs if the current one is stuck
                                return false;
                            }
                            
                            log.warn("Found running pre-payroll job for tenant: {} with execution ID: {} status: {} running for {} minutes", 
                                    tenantId, latestExecution.getId(), latestExecution.getStatus(), runningTime / (60 * 1000));
                            return true;
                        }
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("Error checking if job is already running for tenant: {}", tenantId, e);
            // If we can't check, assume no job is running to avoid blocking legitimate requests
            return false;
        }
    }

    @Override
    @Async("calculationExecutor")
    public CompletableFuture<String> processPayrollBatchAsync(String tenantId, 
                                                            LocalDate payrollPeriodStart, 
                                                            LocalDate payrollPeriodEnd) {
        String result = processPayrollBatch(tenantId, payrollPeriodStart, payrollPeriodEnd);
        return CompletableFuture.completedFuture(result);
    }
}
