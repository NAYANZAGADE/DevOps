package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.common.dto.PrePayrollCalculationDTO;
import com.glidingpath.rules.service.PrePayrollBatchService;
import com.glidingpath.rules.service.PrePayrollCalculationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Pre-Payroll Calculation Controller
 * 
 * Provides endpoints for individual calculations and Spring Batch processing.
 * Spring Batch handles all the complex orchestration automatically.
 */
@RestController
@RequestMapping("/pre-payroll")
@RequiredArgsConstructor
@Slf4j
public class PrePayrollCalculationController {

    private final PrePayrollCalculationService calculationService;
    private final PrePayrollBatchService batchService;

    // ===== INDIVIDUAL CALCULATION ENDPOINTS =====
    
    @PostMapping("/calculate")
    public ResponseEntity<List<PrePayrollCalculationDTO>> calculateForAllEmployees(
            @RequestParam String tenantId,
            @RequestParam LocalDate payrollPeriodStart,
            @RequestParam LocalDate payrollPeriodEnd) {
        
        List<PrePayrollCalculationDTO> results = calculationService.calculateForAllEmployees(
                tenantId, payrollPeriodStart, payrollPeriodEnd);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/calculate/async")
    public ResponseEntity<CompletableFuture<List<PrePayrollCalculationDTO>>> calculateForAllEmployeesAsync(
            @RequestParam String tenantId,
            @RequestParam LocalDate payrollPeriodStart,
            @RequestParam LocalDate payrollPeriodEnd) {
        
        CompletableFuture<List<PrePayrollCalculationDTO>> results = calculationService.calculateForAllEmployeesAsync(
                tenantId, payrollPeriodStart, payrollPeriodEnd);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/reprocess")
    public ResponseEntity<List<PrePayrollCalculationDTO>> reprocessFailedCalculations(
            @RequestParam String tenantId) {
        
        List<PrePayrollCalculationDTO> results = calculationService.reprocessFailedCalculations(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results")
    public ResponseEntity<List<PrePayrollCalculationDTO>> getCalculationsByTenant(
            @RequestParam String tenantId) {
        
        List<PrePayrollCalculationDTO> results = calculationService.getCalculationsByTenant(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/period")
    public ResponseEntity<List<PrePayrollCalculationDTO>> getCalculationsByPayrollPeriod(
            @RequestParam String tenantId,
            @RequestParam LocalDate payrollPeriodStart,
            @RequestParam LocalDate payrollPeriodEnd) {
        
        List<PrePayrollCalculationDTO> results = calculationService.getCalculationsByPayrollPeriod(
                tenantId, payrollPeriodStart, payrollPeriodEnd);
        return ResponseEntity.ok(results);
    }

    // ===== SPRING BATCH PROCESSING ENDPOINTS =====
    
    /**
     * Launch the complete pre-payroll Spring Batch job
     * 
     * Spring Batch automatically handles:
     * 1. Eligibility evaluation for all employees
     * 2. Pre-payroll calculations for eligible employees  
     * 3. Finch deduction creation for successful calculations
     * 
     * IMPORTANT: Only one job can run per tenant at a time
     * 
     * @param tenantId The tenant ID
     * @param payrollPeriodStart Start date of payroll period
     * @param payrollPeriodEnd End date of payroll period
     * @return Job launch status message
     */
    @PostMapping("/batch/process")
    public ResponseEntity<String> processPayrollBatch(
            @RequestParam String tenantId,
            @RequestParam LocalDate payrollPeriodStart,
            @RequestParam LocalDate payrollPeriodEnd) {
        
        try {
            log.info("Received request to launch pre-payroll batch job for tenant: {} period: {} to {}", 
                    tenantId, payrollPeriodStart, payrollPeriodEnd);
            
            String result = batchService.processPayrollBatch(tenantId, payrollPeriodStart, payrollPeriodEnd);
            
            if (result.startsWith("A pre-payroll job is already running")) {
                // Job is already running
                return ResponseEntity.status(409).body(result);
            } else {
                // Job started successfully
                return ResponseEntity.ok(result);
            }
            
        } catch (Exception e) {
            log.error("Failed to launch pre-payroll batch job for tenant: {}", tenantId, e);
            return ResponseEntity.status(500).body("Failed to launch batch job: " + e.getMessage());
        }
    }

    /**
     * Launch the complete pre-payroll Spring Batch job asynchronously
     * 
     * @param tenantId The tenant ID
     * @param payrollPeriodStart Start date of payroll period
     * @param payrollPeriodEnd End date of payroll period
     * @return CompletableFuture with job launch status message
     */
    @PostMapping("/batch/process/async")
    public ResponseEntity<CompletableFuture<String>> processPayrollBatchAsync(
            @RequestParam String tenantId,
            @RequestParam LocalDate payrollPeriodStart,
            @RequestParam LocalDate payrollPeriodEnd) {
        
        CompletableFuture<String> result = batchService.processPayrollBatchAsync(tenantId, payrollPeriodStart, payrollPeriodEnd);
        return ResponseEntity.ok(result);
    }
} 