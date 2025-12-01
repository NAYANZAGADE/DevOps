package com.glidingpath.rules.util;

import com.glidingpath.common.dto.EmployeeEligibilityDTO;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.entity.PlanEligibility;
import com.glidingpath.core.entity.TenantPlan;
import com.glidingpath.core.repository.PlanParticipantRepository;
import com.glidingpath.core.repository.TenantPlanRepository;
import com.glidingpath.rules.util.DroolsRuleEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Comparator;
import java.util.Optional;

/**
 * Shared utility for eligibility processing logic.
 * Used by both Spring Batch components and controllers to eliminate code duplication.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EligibilityProcessingUtility {

    private final PlanParticipantRepository planParticipantRepository;
    private final TenantPlanRepository tenantPlanRepository;
    private final DroolsRuleEvaluator droolsRuleEvaluator;

    /**
     * Convert PlanParticipant entity to EmployeeEligibilityDTO
     */
    public EmployeeEligibilityDTO convertToEligibilityDto(PlanParticipant employee) {
        EmployeeEligibilityDTO dto = new EmployeeEligibilityDTO();
        
        // Core identification
        dto.setEmployeeId(employee.getIndividualId());
        dto.setTenantId(employee.getTenantId());
        dto.setCurrentDate(LocalDate.now());
        
        // Eligibility evaluation data
        dto.setDateOfBirth(employee.getDob());
        dto.setHireDate(employee.getStartDate());
        dto.setRehireDate(employee.getLatestRehireDate());
        dto.setEmploymentStatus(employee.getEmploymentStatus());
        
        // Employment type from simple field
        dto.setEmploymentType(employee.getEmploymentType());
        
        // Calculate age and service duration
        dto.age = calculateAge(employee.getDob());
        dto.monthsOfService = calculateMonthsOfService(employee.getStartDate(), employee.getLatestRehireDate());
        
        // Initialize eligibility fields
        dto.setEligible(false); // Default to false, will be set by rule engine
        dto.setEligibilityReason("Pending evaluation");
        
        return dto;
    }

    /**
     * Get tenant plan for eligibility configuration
     */
    public Optional<TenantPlan> getTenantPlan(String tenantId) {
        return tenantPlanRepository.findByTenantId(tenantId)
                .stream()
                .max(Comparator.comparing(
                    plan -> plan.getCreatedAt() != null ? plan.getCreatedAt() : LocalDateTime.MIN
                ));
    }

    /**
     * Get plan eligibility configuration from tenant plan
     */
    public Optional<PlanEligibility> getPlanEligibility(TenantPlan tenantPlan) {
        return Optional.ofNullable(tenantPlan.getEligibility());
    }

    /**
     * Evaluate eligibility for an employee using Drools
     */
    public EmployeeEligibilityDTO evaluateEligibility(EmployeeEligibilityDTO employeeDto, String tenantId) {
        log.debug("Evaluating eligibility for employee: {} in tenant: {}", 
            employeeDto.getEmployeeId(), tenantId);
        
        try {
            // Get plan eligibility configuration
            Optional<TenantPlan> tenantPlanOpt = getTenantPlan(tenantId);
            if (tenantPlanOpt.isEmpty()) {
                log.warn("No tenant plan found for tenant: {}", tenantId);
                employeeDto.setEligibilityReason("No plan configuration found");
                return employeeDto;
            }
            
            TenantPlan tenantPlan = tenantPlanOpt.get();
            Optional<PlanEligibility> planEligibilityOpt = getPlanEligibility(tenantPlan);
            
            if (planEligibilityOpt.isEmpty()) {
                log.warn("No plan eligibility configuration found for tenant: {}", tenantId);
                employeeDto.setEligibilityReason("No eligibility configuration found");
                return employeeDto;
            }
            
            PlanEligibility planEligibility = planEligibilityOpt.get();
            
            // Set public fields for Drools compatibility
            employeeDto.age = employeeDto.getAge();
            employeeDto.monthsOfService = employeeDto.getMonthsOfService();
            
            log.debug("DTO before Drools: employeeId={}, age={}, monthsOfService={}, employmentStatus={}, employmentType={}, eligible={}", 
                    employeeDto.getEmployeeId(), employeeDto.age, employeeDto.monthsOfService, 
                    employeeDto.getEmploymentStatus(), employeeDto.getEmploymentType(), employeeDto.isEligible());
            
            // Evaluate using Drools
            return droolsRuleEvaluator.evaluateEligibility(employeeDto, planEligibility);
            
        } catch (Exception e) {
            log.error("Error evaluating eligibility for employee: {} in tenant: {}", employeeDto.getEmployeeId(), tenantId, e);
            employeeDto.setEligibilityReason("Error during eligibility evaluation: " + e.getMessage());
            return employeeDto;
        }
    }

    /**
     * Evaluate eligibility for an employee by ID
     */
    public EmployeeEligibilityDTO evaluateEligibilityById(String employeeId, String tenantId) {
        Optional<PlanParticipant> employeeOpt = planParticipantRepository.findByIndividualIdAndTenantId(employeeId, tenantId);
        if (employeeOpt.isEmpty()) {
            log.warn("Employee not found: {} in tenant: {}", employeeId, tenantId);
            EmployeeEligibilityDTO failedDto = new EmployeeEligibilityDTO();
            failedDto.setEmployeeId(employeeId);
            failedDto.setTenantId(tenantId);
            failedDto.setEligibilityReason("Employee not found");
            return failedDto;
        }
        
        PlanParticipant employee = employeeOpt.get();
        EmployeeEligibilityDTO dto = convertToEligibilityDto(employee);
        return evaluateEligibility(dto, tenantId);
    }

    /**
     * Calculate employee age based on date of birth
     */
    public int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null || LocalDate.now() == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Calculate months of service
     */
    public int calculateMonthsOfService(LocalDate hireDate, LocalDate rehireDate) {
        LocalDate startDate = rehireDate != null ? rehireDate : hireDate;
        if (startDate == null || LocalDate.now() == null) {
            return 0;
        }
        Period period = Period.between(startDate, LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }

    /**
     * Create a failed eligibility result when processing fails
     */
    public EmployeeEligibilityDTO createFailedEligibilityResult(PlanParticipant employee, Exception error, String tenantId) {
        EmployeeEligibilityDTO failedDto = new EmployeeEligibilityDTO();
        failedDto.setEmployeeId(employee.getIndividualId());
        failedDto.setTenantId(tenantId);
        failedDto.setEligible(false);
        failedDto.setEligibilityReason("Error during eligibility check: " + error.getMessage());
        failedDto.setCurrentDate(LocalDate.now());
        
        // Set calculated fields to 0 for failed cases
        failedDto.age = 0;
        failedDto.monthsOfService = 0;
        
        return failedDto;
    }
}
