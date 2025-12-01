package com.glidingpath.rules.service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

/**
 * Spring Batch Job Launcher Service for Pre-Payroll Processing
 * 
 * This service directly launches Spring Batch jobs without custom orchestration.
 * Spring Batch handles all the job management, step execution, and error handling.
 */
public interface PrePayrollBatchService {

    /**
     * Launch the complete pre-payroll Spring Batch job
     * 
     * @param tenantId The tenant ID for the batch job
     * @param payrollPeriodStart Start date of the payroll period
     * @param payrollPeriodEnd End date of the payroll period
     * @return Job execution status message
     */
    String processPayrollBatch(String tenantId, LocalDate payrollPeriodStart, LocalDate payrollPeriodEnd);

    /**
     * Launch the complete pre-payroll Spring Batch job asynchronously
     * 
     * @param tenantId The tenant ID for the batch job
     * @param payrollPeriodStart Start date of the payroll period
     * @param payrollPeriodEnd End date of the payroll period
     * @return CompletableFuture with job execution status message
     */
    CompletableFuture<String> processPayrollBatchAsync(String tenantId, LocalDate payrollPeriodStart, LocalDate payrollPeriodEnd);
}
