package com.glidingpath.rules.contributions.processor;

import com.glidingpath.common.dto.EmployeeEligibilityDTO;
import com.glidingpath.common.dto.PrePayrollCalculationDTO;
import com.glidingpath.common.dto.PrePayrollCalculationFact;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.entity.TenantPlan;
import com.glidingpath.core.repository.PlanParticipantRepository;
import com.glidingpath.core.repository.TenantPlanRepository;
import com.glidingpath.rules.util.DroolsRuleEvaluator;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Comparator;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalculationBatchProcessor implements ItemProcessor<EmployeeEligibilityDTO, PrePayrollCalculationDTO> {


    private final DroolsRuleEvaluator droolsRuleEvaluator;
    private final PlanParticipantRepository planParticipantRepository;
    private final TenantPlanRepository tenantPlanRepository;
    
    private String tenantId;
    private LocalDate payrollPeriodStart;
    private LocalDate payrollPeriodEnd;
    private int processedCount = 0;
    private int successCount = 0;
    private int failureCount = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        this.payrollPeriodStart = LocalDate.parse(
            stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_PAYROLL_PERIOD_START)
        );
        this.payrollPeriodEnd = LocalDate.parse(
            stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_PAYROLL_PERIOD_END)
        );
        
        this.processedCount = 0;
        this.successCount = 0;
        this.failureCount = 0;
        
        log.info("Initializing CalculationBatchProcessor for tenant: {} period: {} to {}", 
                tenantId, payrollPeriodStart, payrollPeriodEnd);
        
        if (tenantId == null || payrollPeriodStart == null || payrollPeriodEnd == null) {
            throw new IllegalStateException("Tenant ID and payroll period dates are required for calculation batch processing");
        }
    }

    @Override
    public PrePayrollCalculationDTO process(EmployeeEligibilityDTO eligibleEmployee) throws Exception {
        if (eligibleEmployee == null) {
            return null;
        }

        processedCount++;
        
        try {
            log.debug("Processing calculation for eligible employee: {} ({} of batch)", 
                    eligibleEmployee.getEmployeeId(), processedCount);
            
            // Validate that the employee is actually eligible
            if (!eligibleEmployee.isEligible()) {
                log.warn("Employee {} is marked as not eligible, skipping calculation", eligibleEmployee.getEmployeeId());
                failureCount++;
                throw new IllegalStateException("Employee not eligible for benefits: " + eligibleEmployee.getEmployeeId());
            }
            
            // Get employee entity and tenant plan for calculations
            Optional<PlanParticipant> employeeOpt = planParticipantRepository.findByIndividualId(eligibleEmployee.getEmployeeId());
            if (employeeOpt.isEmpty()) {
                log.error("Employee entity not found for ID: {}", eligibleEmployee.getEmployeeId());
                failureCount++;
                throw new IllegalStateException("Employee entity not found: " + eligibleEmployee.getEmployeeId());
            }
            
            PlanParticipant employee = employeeOpt.get();
            TenantPlan tenantPlan = getTenantPlan(tenantId);
            if (tenantPlan == null) {
                log.error("Tenant plan not found for tenant: {}", tenantId);
                failureCount++;
                throw new IllegalStateException("Tenant plan not found: " + tenantId);
            }
            
            // Perform the actual calculation
            PrePayrollCalculationDTO calculationResult = performCalculation(employee, eligibleEmployee, tenantPlan);
            
            successCount++;
            
            // Force Spring Batch to recognize this as a processing operation
            // This helps with proper counting in the monitoring
            if (processedCount % BatchConstants.CALCULATION_CHUNK_SIZE == 0) {
                log.info("Completed calculation processing chunk: {}/{} eligible employees processed (Success: {}, Failed: {})", 
                        processedCount, "batch", successCount, failureCount);
            }
            
            return calculationResult;
            
        } catch (Exception e) {
            failureCount++;
            log.error("Failed to process calculation for employee: {} in tenant: {}", 
                    eligibleEmployee.getEmployeeId(), tenantId, e);
            log.error("Exception details: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            
            // Create failed calculation result instead of throwing exception
            // This allows the batch to continue processing other employees
            return createFailedCalculationResult(eligibleEmployee, e);
        }
    }

    /**
     * Create Drools fact for pre-payroll calculation
     */
    private PrePayrollCalculationFact createDroolsFact(PlanParticipant employee, TenantPlan tenantPlan) {
        PrePayrollCalculationFact fact = new PrePayrollCalculationFact();
        
        fact.setEmployeeId(employee.getIndividualId());
        
        // Set compensation data with proper BigDecimal conversion
        if (employee.getIncomeAmount() != null) {
            fact.setEmployeeAnnualCompensation(BigDecimal.valueOf(employee.getIncomeAmount()));
            fact.setEligibleCompensation(BigDecimal.valueOf(employee.getIncomeAmount()));
        } else {
            fact.setEmployeeAnnualCompensation(BigDecimal.ZERO);
            fact.setEligibleCompensation(BigDecimal.ZERO);
        }
        
        // Set employee contribution configuration
        if (tenantPlan.getEmployeeContributionConfig() != null) {
            var config = tenantPlan.getEmployeeContributionConfig();
            if (config.getEnrollmentStartRate() != null) {
                fact.setEmployeeContributionPercent(BigDecimal.valueOf(config.getEnrollmentStartRate()));
            } else {
                fact.setEmployeeContributionPercent(BigDecimal.ZERO);
            }
            
            // Set auto-enrollment fields
            fact.setAutoEnrollmentEnabled(config.getIsAutoEnrollment() != null ? config.getIsAutoEnrollment() : false);
            fact.setAutoEnrollmentPercent(config.getEnrollmentStartRate() != null ? 
                BigDecimal.valueOf(config.getEnrollmentStartRate()) : new BigDecimal("3.0"));
        } else {
            fact.setEmployeeContributionPercent(BigDecimal.ZERO);
            fact.setAutoEnrollmentEnabled(false);
            fact.setAutoEnrollmentPercent(new BigDecimal("3.0"));
        }
        
        // Set employer match configuration
        if (tenantPlan.getEmployerContributionRule() != null) {
            var rule = tenantPlan.getEmployerContributionRule();
            fact.setEmployerMatchPercent(rule.getMatchPercentage() != null ? 
                BigDecimal.valueOf(rule.getMatchPercentage()) : BigDecimal.ZERO);
            fact.setEmployerMatchType(rule.getRuleType());
        } else {
            fact.setEmployerMatchPercent(BigDecimal.ZERO);
            fact.setEmployerMatchType("NO_MATCH");
        }
        
        // Set profit sharing configuration
        if (tenantPlan.getProfitSharingConfig() != null) {
            var config = tenantPlan.getProfitSharingConfig();
            fact.setProfitSharingPercent(config.getProRataPercentage() != null ? 
                BigDecimal.valueOf(config.getProRataPercentage()) : BigDecimal.ZERO);
        } else {
            fact.setProfitSharingPercent(BigDecimal.ZERO);
        }
        
        // Set compensation limits
        fact.setCompensationLimit(BigDecimal.valueOf(315000)); // 2024 IRS limit
        
        // Set employment status and eligibility data
        fact.setEmploymentStatus(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus() : "UNKNOWN");
        fact.setAge(employee.getDob() != null ? 
            java.time.Period.between(employee.getDob(), LocalDate.now()).getYears() : 0);
        fact.setMonthsOfService(calculateMonthsOfService(employee.getStartDate(), employee.getLatestRehireDate()));
        fact.setMinimumServiceMonths(1); // Default minimum
        fact.setMinimumAge(21); // Default minimum age
        
        // Set plan type
        fact.setPlanType(tenantPlan.getPlanTypeId() != null ? tenantPlan.getPlanTypeId().toString() : "UNKNOWN");
        
        // Initialize calculation reason
        fact.setCalculationReason("Initialized for calculation");
        
        return fact;
    }
    
    /**
     * Calculate months of service
     */
    private int calculateMonthsOfService(LocalDate hireDate, LocalDate rehireDate) {
        LocalDate startDate = rehireDate != null ? rehireDate : hireDate;
        if (startDate == null || LocalDate.now() == null) {
            return 0;
        }
        java.time.Period period = java.time.Period.between(startDate, LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }

    /**
     * Convert Drools fact results to DTO
     */
    private PrePayrollCalculationDTO convertFactToDTO(PrePayrollCalculationFact fact, 
                                                     EmployeeEligibilityDTO eligibleEmployee, 
                                                     PlanParticipant employee,
                                                     TenantPlan tenantPlan) {
        
        PrePayrollCalculationDTO dto = new PrePayrollCalculationDTO();
        
        // Basic identification
        dto.setCalculationId("CALC-" + UUID.randomUUID().toString());
        dto.setTenantId(tenantId);
        dto.setEmployeeId(eligibleEmployee.getEmployeeId());
        dto.setCalculationDate(LocalDateTime.now());
        dto.setStatus("SUCCESS");
        
        // Payroll period
        dto.setPayrollPeriodStart(payrollPeriodStart);
        dto.setPayrollPeriodEnd(payrollPeriodEnd);
        
        // Employee contribution calculations - use Drools results or fallback
        if (fact.getEmployeeContribution() != null && fact.getEmployeeContribution().compareTo(BigDecimal.ZERO) > 0) {
            dto.setEmployeeContributionAmount(fact.getEmployeeContribution());
            dto.setEmployeeContributionPercentage(fact.getEmployeeContributionPercent() != null ? 
                fact.getEmployeeContributionPercent() : BigDecimal.ZERO);
        } else {
            // Fallback to manual calculation
            var employeeContribution = calculateEmployeeContribution(employee, tenantPlan);
            dto.setEmployeeContributionAmount(employeeContribution.getAmount());
            dto.setEmployeeContributionPercentage(employeeContribution.getPercentage());
        }
        
        // Employer match calculations - use Drools results or fallback
        if (fact.getEmployerContribution() != null && fact.getEmployerContribution().compareTo(BigDecimal.ZERO) > 0) {
            dto.setEmployerMatchAmount(fact.getEmployerContribution());
            dto.setEmployerMatchPercentage(fact.getEmployerMatchPercent() != null ? 
                fact.getEmployerMatchPercent() : BigDecimal.ZERO);
        } else {
            // Fallback to manual calculation
            var employerMatch = calculateEmployerMatch(employee, tenantPlan, dto.getEmployeeContributionAmount());
            dto.setEmployerMatchAmount(employerMatch.getAmount());
            dto.setEmployerMatchPercentage(employerMatch.getPercentage());
        }
        
        // Profit sharing calculations - use Drools results or fallback
        if (fact.getProfitSharingContribution() != null && fact.getProfitSharingContribution().compareTo(BigDecimal.ZERO) > 0) {
            dto.setProfitSharingAmount(fact.getProfitSharingContribution());
            dto.setProfitSharingPercentage(fact.getProfitSharingPercent() != null ? 
                fact.getProfitSharingPercent() : BigDecimal.ZERO);
        } else {
            // Fallback to manual calculation
            var profitSharing = calculateProfitSharing(employee, tenantPlan);
            dto.setProfitSharingAmount(profitSharing.getAmount());
            dto.setProfitSharingPercentage(profitSharing.getPercentage());
        }
        
        // Total calculations - ensure no null values
        BigDecimal employeeAmount = dto.getEmployeeContributionAmount() != null ? dto.getEmployeeContributionAmount() : BigDecimal.ZERO;
        BigDecimal employerAmount = dto.getEmployerMatchAmount() != null ? dto.getEmployerMatchAmount() : BigDecimal.ZERO;
        BigDecimal profitSharingAmount = dto.getProfitSharingAmount() != null ? dto.getProfitSharingAmount() : BigDecimal.ZERO;
        
        dto.setTotalContributionAmount(employeeAmount.add(employerAmount).add(profitSharingAmount));
        
        // Calculate total percentage if compensation is available
        BigDecimal totalCompensation = fact.getEligibleCompensation() != null ? fact.getEligibleCompensation() : BigDecimal.ZERO;
        if (totalCompensation.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalAmount = dto.getTotalContributionAmount();
            dto.setTotalContributionPercentage(totalAmount
                .multiply(new BigDecimal("100"))
                .divide(totalCompensation, 2, RoundingMode.HALF_UP));
        } else {
            dto.setTotalContributionPercentage(BigDecimal.ZERO);
        }
        
        // Base salary and compensation - ensure no null values
        dto.setBaseSalary(fact.getEmployeeAnnualCompensation() != null ? fact.getEmployeeAnnualCompensation() : BigDecimal.ZERO);
        dto.setEligibleCompensation(fact.getEligibleCompensation() != null ? fact.getEligibleCompensation() : BigDecimal.ZERO);
        
        // Plan configuration references
        dto.setPlanId(tenantPlan.getId());
        if (tenantPlan.getEmployerContributionRule() != null) {
            dto.setEmployerContributionRuleId(tenantPlan.getEmployerContributionRule().getId());
        }
        if (tenantPlan.getEmployeeContributionConfig() != null) {
            dto.setEmployeeContributionConfigId(tenantPlan.getEmployeeContributionConfig().getId());
        }
        if (tenantPlan.getProfitSharingConfig() != null) {
            dto.setProfitSharingConfigId(tenantPlan.getProfitSharingConfig().getId());
        }
        
        // Finch integration fields - initialize with defaults
        dto.setFinchStatus("PENDING");
        dto.setFinchErrorMessage(null);
        dto.setFinchBenefitId(null);
        dto.setFinchEmployerBenefitId(null);
        dto.setFinchProfitSharingBenefitId(null);
        dto.setFinchProcessedAt(null);
        
        return dto;
    }

    /**
     * Calculate employee contribution with fallback logic
     */
    private ContributionResult calculateEmployeeContribution(PlanParticipant employee, TenantPlan tenantPlan) {
        BigDecimal contributionPercentage = BigDecimal.ZERO;
        BigDecimal contributionAmount = BigDecimal.ZERO;
        
        if (tenantPlan.getEmployeeContributionConfig() != null) {
            var config = tenantPlan.getEmployeeContributionConfig();
            
            // Get base contribution percentage
            if (config.getEnrollmentStartRate() != null) {
                contributionPercentage = BigDecimal.valueOf(config.getEnrollmentStartRate());
            }
            
            // Auto-enrollment logic - only apply if no contribution is set
            if (config.getIsAutoEnrollment() != null && config.getIsAutoEnrollment() && 
                contributionPercentage.compareTo(BigDecimal.ZERO) == 0) {
                contributionPercentage = config.getEnrollmentStartRate() != null ? 
                        BigDecimal.valueOf(config.getEnrollmentStartRate()) : new BigDecimal("3.0");
            }
            
            // Max rate enforcement
            if (config.getEnrollmentMaxRate() != null && 
                contributionPercentage.compareTo(BigDecimal.valueOf(config.getEnrollmentMaxRate())) > 0) {
                contributionPercentage = BigDecimal.valueOf(config.getEnrollmentMaxRate());
            }
        }
        
        // Get compensation from PlanParticipant entity
        BigDecimal eligibleCompensation = getEmployeeCompensation(employee);
        
        // Calculate amount based on compensation
        if (eligibleCompensation.compareTo(BigDecimal.ZERO) > 0 && contributionPercentage.compareTo(BigDecimal.ZERO) > 0) {
            contributionAmount = eligibleCompensation
                    .multiply(contributionPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        
        return new ContributionResult(contributionAmount, contributionPercentage);
    }

    /**
     * Calculate employer match with fallback logic
     */
    private ContributionResult calculateEmployerMatch(PlanParticipant employee, TenantPlan tenantPlan, BigDecimal employeeContribution) {
        if (tenantPlan.getEmployerContributionRule() == null) {
            return new ContributionResult(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        var rule = tenantPlan.getEmployerContributionRule();
        // Get compensation from PlanParticipant entity
        BigDecimal eligibleCompensation = getEmployeeCompensation(employee);
        
        BigDecimal matchAmount = BigDecimal.ZERO;
        BigDecimal matchPercentage = BigDecimal.ZERO;
        
        // Handle different rule types
        if ("MATCH".equals(rule.getRuleType()) || "BASIC_MATCH".equals(rule.getRuleType())) {
            matchAmount = calculateBasicMatch(employeeContribution, eligibleCompensation, rule);
        } else if ("PERCENTAGE_MATCH".equals(rule.getRuleType())) {
            matchAmount = calculatePercentageMatch(employeeContribution, eligibleCompensation, rule);
        }
        
        // Calculate percentage
        if (eligibleCompensation.compareTo(BigDecimal.ZERO) > 0) {
            matchPercentage = matchAmount
                    .multiply(new BigDecimal("100"))
                    .divide(eligibleCompensation, 2, RoundingMode.HALF_UP);
        }
        
        return new ContributionResult(matchAmount, matchPercentage);
    }

    /**
     * Calculate basic match (percentage of employee contribution)
     */
    private BigDecimal calculateBasicMatch(BigDecimal employeeContribution, BigDecimal eligibleCompensation, 
                                         com.glidingpath.core.entity.EmployerContributionRule rule) {
        BigDecimal matchRate = rule.getMatchPercentage() != null ? 
                BigDecimal.valueOf(rule.getMatchPercentage()) : BigDecimal.ZERO;
        BigDecimal matchLimit = rule.getMatchLimitPercent() != null ? 
                BigDecimal.valueOf(rule.getMatchLimitPercent()) : BigDecimal.ZERO;
        
        BigDecimal matchAmount = employeeContribution.multiply(matchRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // Apply limit if specified
        if (matchLimit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal limitAmount = eligibleCompensation.multiply(matchLimit)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (matchAmount.compareTo(limitAmount) > 0) {
                matchAmount = limitAmount;
            }
        }
        
        return matchAmount;
    }

    /**
     * Calculate percentage match (percentage of eligible compensation)
     */
    private BigDecimal calculatePercentageMatch(BigDecimal employeeContribution, BigDecimal eligibleCompensation,
                                              com.glidingpath.core.entity.EmployerContributionRule rule) {
        BigDecimal matchRate = rule.getMatchPercentage() != null ? 
                BigDecimal.valueOf(rule.getMatchPercentage()) : BigDecimal.ZERO;
        BigDecimal matchLimit = rule.getMatchLimitPercent() != null ? 
                BigDecimal.valueOf(rule.getMatchLimitPercent()) : BigDecimal.ZERO;
        
        BigDecimal matchAmount = eligibleCompensation.multiply(matchRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // Apply limit if specified
        if (matchLimit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal limitAmount = eligibleCompensation.multiply(matchLimit)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (matchAmount.compareTo(limitAmount) > 0) {
                matchAmount = limitAmount;
            }
        }
        
        return matchAmount;
    }

    /**
     * Calculate profit sharing with fallback logic
     */
    private ContributionResult calculateProfitSharing(PlanParticipant employee, TenantPlan tenantPlan) {
        if (tenantPlan.getProfitSharingConfig() == null) {
            return new ContributionResult(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        var config = tenantPlan.getProfitSharingConfig();
        // Get compensation from PlanParticipant entity
        BigDecimal eligibleCompensation = getEmployeeCompensation(employee);
        
        BigDecimal profitSharingAmount = BigDecimal.ZERO;
        BigDecimal profitSharingPercentage = BigDecimal.ZERO;
        
        // Pro-rata calculation
        if (config.getProRataPercentage() != null) {
            profitSharingPercentage = BigDecimal.valueOf(config.getProRataPercentage());
            profitSharingAmount = eligibleCompensation.multiply(profitSharingPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        
        return new ContributionResult(profitSharingAmount, profitSharingPercentage);
    }

    /**
     * Extract employee compensation from PlanParticipant entity
     */
    private BigDecimal getEmployeeCompensation(PlanParticipant employee) {
        if (employee.getIncomeAmount() != null) {
            return BigDecimal.valueOf(employee.getIncomeAmount());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Helper class for calculation results
     */
    private static class ContributionResult {
        private final BigDecimal amount;
        private final BigDecimal percentage;
        
        public ContributionResult(BigDecimal amount, BigDecimal percentage) {
            this.amount = amount != null ? amount : BigDecimal.ZERO;
            this.percentage = percentage != null ? percentage : BigDecimal.ZERO;
        }
        
        public BigDecimal getAmount() { return amount; }
        public BigDecimal getPercentage() { return percentage; }
    }

    /**
     * Get tenant plan for calculations
     */
    private TenantPlan getTenantPlan(String tenantId) {
        return tenantPlanRepository.findByTenantId(tenantId)
                .stream()
                .max(Comparator.comparing(TenantPlan::getEffectiveDate))
                .orElse(null);
    }

    /**
     * Perform the actual calculation for an employee
     */
    private PrePayrollCalculationDTO performCalculation(PlanParticipant employee, EmployeeEligibilityDTO eligibleEmployee, TenantPlan tenantPlan) {
        // Create Drools fact and perform calculations
        PrePayrollCalculationFact fact = createDroolsFact(employee, tenantPlan);
        
        // Debug logging to track fact creation
        log.debug("Created Drools fact for employee {}: compensation={}, contributionPercent={}, employerMatchPercent={}, profitSharingPercent={}", 
                employee.getIndividualId(),
                fact.getEmployeeAnnualCompensation(),
                fact.getEmployeeContributionPercent(),
                fact.getEmployerMatchPercent(),
                fact.getProfitSharingPercent());
        
        droolsRuleEvaluator.evaluatePrePayrollCalculation(fact, tenantPlan);
        
        // Debug logging after Drools evaluation
        log.debug("After Drools evaluation for employee {}: employeeContribution={}, employerContribution={}, profitSharingContribution={}", 
                employee.getIndividualId(),
                fact.getEmployeeContribution(),
                fact.getEmployerContribution(),
                fact.getProfitSharingContribution());
        
        // Convert Drools results to DTO
        PrePayrollCalculationDTO result = convertFactToDTO(fact, eligibleEmployee, employee, tenantPlan);
        
        // Debug logging for final result
        log.debug("Final calculation result for employee {}: employeeAmount={}, employerAmount={}, profitSharingAmount={}, totalAmount={}", 
                eligibleEmployee.getEmployeeId(), 
                result.getEmployeeContributionAmount(),
                result.getEmployerMatchAmount(),
                result.getProfitSharingAmount(),
                result.getTotalContributionAmount());
        
        return result;
    }

    /**
     * Create a failed calculation result when processing fails
     */
    private PrePayrollCalculationDTO createFailedCalculationResult(EmployeeEligibilityDTO eligibleEmployee, Exception error) {
        PrePayrollCalculationDTO failedResult = new PrePayrollCalculationDTO();
        failedResult.setEmployeeId(eligibleEmployee.getEmployeeId());
        failedResult.setTenantId(eligibleEmployee.getTenantId());
        failedResult.setStatus("FAILED");
        failedResult.setErrorMessage("Calculation failed: " + error.getMessage());
        failedResult.setCalculationDate(LocalDateTime.now());
        failedResult.setCalculationId(UUID.randomUUID().toString());
        return failedResult;
    }


    /**
     * Get processing statistics
     */
    public String getProcessingStats() {
        return String.format("Processed: %d, Success: %d, Failure: %d", 
                processedCount, successCount, failureCount);
    }
}
