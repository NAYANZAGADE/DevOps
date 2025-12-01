package com.glidingpath.rules.service;

import com.glidingpath.common.dto.PrePayrollCalculationDTO;
import com.glidingpath.common.dto.EmployeeEligibilityDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * PrePayrollCalculationService Interface
 * 
 * All methods now consistently use Spring Batch for processing.
 * Returns job status information instead of direct calculation results.
 * 
 * Spring Batch handles:
 * - Eligibility evaluation
 * - Pre-payroll calculations  
 * - Finch deduction creation
 * - Database persistence
 */
public interface PrePayrollCalculationService {

    /**
     * Launch Spring Batch job for all employees in a tenant
     * 
     * @param tenantId The tenant identifier
     * @param payrollPeriodStart The start date of the payroll period
     * @param payrollPeriodEnd The end date of the payroll period
     * @return Job status information (not calculation results)
     */
    List<PrePayrollCalculationDTO> calculateForAllEmployees(String tenantId, 
                                                          LocalDate payrollPeriodStart, 
                                                          LocalDate payrollPeriodEnd);

    /**
     * Launch Spring Batch job for pre-filtered eligible employees
     * 
     * @param tenantId The tenant identifier
     * @param payrollPeriodStart The start date of the payroll period
     * @param payrollPeriodEnd The end date of the payroll period
     * @param eligibleEmployees List of pre-filtered eligible employees
     * @return Job status information (not calculation results)
     */
    List<PrePayrollCalculationDTO> calculateForEligibleEmployees(String tenantId, 
                                                               LocalDate payrollPeriodStart, 
                                                               LocalDate payrollPeriodEnd,
                                                               List<EmployeeEligibilityDTO> eligibleEmployees);

    /**
     * Launch Spring Batch job asynchronously for all employees
     * 
     * @param tenantId The tenant identifier
     * @param payrollPeriodStart The start date of the payroll period
     * @param payrollPeriodEnd The end date of the payroll period
     * @return CompletableFuture with job status information
     */
    CompletableFuture<List<PrePayrollCalculationDTO>> calculateForAllEmployeesAsync(String tenantId, 
                                                                                   LocalDate payrollPeriodStart, 
                                                                                   LocalDate payrollPeriodEnd);
    
    /**
     * Launch Spring Batch job to reprocess failed calculations
     * 
     * @param tenantId The tenant identifier
     * @return Job status information
     */
    List<PrePayrollCalculationDTO> reprocessFailedCalculations(String tenantId);
    
    /**
     * Get calculation results for a tenant (from Spring Batch job history)
     * 
     * @param tenantId The tenant identifier
     * @return Job status information or placeholder message
     */
    List<PrePayrollCalculationDTO> getCalculationsByTenant(String tenantId);
    
    /**
     * Get calculation results for a specific payroll period (from Spring Batch job history)
     * 
     * @param tenantId The tenant identifier
     * @param payrollPeriodStart The start date of the payroll period
     * @param payrollPeriodEnd The end date of the payroll period
     * @return Job status information or placeholder message
     */
    List<PrePayrollCalculationDTO> getCalculationsByPayrollPeriod(String tenantId, 
                                                                LocalDate payrollPeriodStart, 
                                                                LocalDate payrollPeriodEnd);
} 