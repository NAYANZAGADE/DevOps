package com.glidingpath.rules.util;

import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.stereotype.Component;

import com.glidingpath.common.dto.EmployeeEligibilityDTO;
import com.glidingpath.common.dto.PrePayrollCalculationFact;
import com.glidingpath.core.entity.PlanEligibility;
import com.glidingpath.core.entity.TenantPlan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DroolsRuleEvaluator {
    private final KieContainer kieContainer;

    public EmployeeEligibilityDTO evaluateEligibility(EmployeeEligibilityDTO employeeDto, PlanEligibility planEligibility) {
        try {
            StatelessKieSession statelessSession = null;
            
            // Try to get the named session first (this should work with our DroolsConfig)
            try {
                statelessSession = kieContainer.newStatelessKieSession("ksession-eligibility");
                log.info("Successfully created named session 'ksession-eligibility' for employee: {}", employeeDto.getEmployeeId());
            } catch (Exception e) {
                log.warn("Named session 'ksession-eligibility' not found, trying default session", e);
            }
            
            // Fallback to default session if named session failed
            if (statelessSession == null) {
                try {
                    statelessSession = kieContainer.newStatelessKieSession();
                    log.warn("Using default session as fallback for employee: {}", employeeDto.getEmployeeId());
                } catch (Exception e) {
                    log.error("Failed to create default session", e);
                }
            }
            
            if (statelessSession == null) {
                throw new IllegalStateException("Failed to create any Drools session");
            }
            
            statelessSession.setGlobal("planEligibility", planEligibility);
            log.info("DTO before Drools: employeeId={}, eligible={}, age={}, monthsOfService={}", 
                    employeeDto.getEmployeeId(), employeeDto.isEligible(), employeeDto.getAge(), employeeDto.getMonthsOfService());
            
            // Cleaner fact execution using List.of()
            statelessSession.execute(List.of(employeeDto));
            
            log.info("Successfully evaluated eligibility using Drools for employee: {}", employeeDto.getEmployeeId());
            // Don't override the eligibility reason - let the rules handle it
            return employeeDto;
        } catch (Exception e) {
            log.error("Error during Drools rule evaluation for employee {}: {}", employeeDto.getEmployeeId(), e.getMessage(), e);
            employeeDto.setEligibilityReason("Error during rule evaluation: " + e.getMessage());
            return employeeDto;
        }
    }

    public void evaluatePrePayrollCalculation(PrePayrollCalculationFact fact, TenantPlan tenantPlan) {
        try {
            log.debug("Evaluating pre-payroll calculation rules for employee: {}", fact.getEmployeeId());
            
            StatelessKieSession statelessSession;
            try {
                statelessSession = kieContainer.newStatelessKieSession("ksession-prepayroll");
            } catch (Exception e) {
                log.warn("Named session 'ksession-prepayroll' not found, falling back to default session", e);
                statelessSession = kieContainer.newStatelessKieSession();
            }
            
            // Set all required globals for payroll rules
            statelessSession.setGlobal("tenantPlan", tenantPlan);
            
            // Set additional globals if available in tenantPlan
            if (tenantPlan.getEligibility() != null) {
                statelessSession.setGlobal("employerRule", tenantPlan.getEligibility());
            }
            
            // Cleaner fact execution using List.of()
            statelessSession.execute(List.of(fact));
            
            log.debug("Drools rule evaluation completed for employee: {}", fact.getEmployeeId());
            
        } catch (Exception e) {
            log.error("Error during Drools rule evaluation for employee: {}", fact.getEmployeeId(), e);
            // Don't throw exception - let the fallback calculation methods handle it
        }
    }
} 