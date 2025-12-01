-- Migration: V18__pre_payroll_rules.sql
-- Description: Add pre-payroll calculation rules (simplified version)

-- Insert pre-payroll calculation rules
INSERT INTO rules_config (name, rule_content, last_updated) VALUES 
('prepayroll', 
'
package com.glidingpath.common.dto;

import com.glidingpath.common.dto.PrePayrollCalculationFact;
import com.glidingpath.core.entity.TenantPlan;
import com.glidingpath.core.entity.EmployerContributionRule;
import com.glidingpath.core.entity.EmployeeContributionConfig;
import com.glidingpath.core.entity.ProfitSharingConfig;
import java.math.BigDecimal;

// Global variables for plan configurations
global TenantPlan tenantPlan;
global EmployerContributionRule employerRule;
global EmployeeContributionConfig employeeRule;
global ProfitSharingConfig profitSharingRule;

// Rule 1: Calculate Employee Contribution
rule "Calculate Employee Contribution"
when
    $fact : PrePayrollCalculationFact(
        employeeAnnualCompensation != null,
        employeeContributionPercent != null
    )
then
    BigDecimal employeeContribution = $fact.getEmployeeAnnualCompensation().multiply($fact.getEmployeeContributionPercent().divide(BigDecimal.valueOf(100)));
    $fact.setEmployeeContribution(employeeContribution);
    $fact.setCalculationReason("Employee contribution calculated: " + $fact.getEmployeeContributionPercent() + "% of " + $fact.getEmployeeAnnualCompensation());
end

// Rule 2: Calculate Employer Match
rule "Calculate Employer Match"
when
    $fact : PrePayrollCalculationFact(
        employeeContribution != null,
        employeeContribution.compareTo(BigDecimal.ZERO) > 0,
        employerMatchPercent != null
    )
then
    BigDecimal employerMatch = $fact.getEmployeeContribution().multiply($fact.getEmployerMatchPercent().divide(BigDecimal.valueOf(100)));
    $fact.setEmployerContribution(employerMatch);
    $fact.setCalculationReason($fact.getCalculationReason() + "; Employer match: " + $fact.getEmployerMatchPercent() + "% of employee contribution");
end

// Rule 3: Calculate Profit Sharing
rule "Calculate Profit Sharing"
when
    $fact : PrePayrollCalculationFact(
        employeeAnnualCompensation != null,
        profitSharingPercent != null
    )
then
    BigDecimal profitSharing = $fact.getEmployeeAnnualCompensation().multiply($fact.getProfitSharingPercent().divide(BigDecimal.valueOf(100)));
    $fact.setProfitSharingContribution(profitSharing);
    $fact.setCalculationReason($fact.getCalculationReason() + "; Profit sharing: " + $fact.getProfitSharingPercent() + "% of compensation");
end

// Rule 4: Apply Auto-Enrollment
rule "Apply Auto Enrollment"
when
    $fact : PrePayrollCalculationFact(
        autoEnrollmentEnabled == true,
        employeeContributionPercent != null,
        autoEnrollmentPercent != null,
        employeeContributionPercent.compareTo(autoEnrollmentPercent) < 0
    )
then
    $fact.setEmployeeContributionPercent($fact.getAutoEnrollmentPercent());
    $fact.setCalculationReason($fact.getCalculationReason() + "; Auto-enrolled at " + $fact.getAutoEnrollmentPercent() + "%");
end

// Rule 5: Apply Compensation Limits
rule "Apply Compensation Limits"
when
    $fact : PrePayrollCalculationFact(
        employeeAnnualCompensation != null,
        compensationLimit != null,
        employeeAnnualCompensation.compareTo(compensationLimit) > 0
    )
then
    $fact.setEligibleCompensation($fact.getCompensationLimit());
    $fact.setCalculationReason($fact.getCalculationReason() + "; Compensation capped at " + $fact.getCompensationLimit());
end

// Rule 6: Calculate Total Contributions
rule "Calculate Total Contributions"
when
    $fact : PrePayrollCalculationFact(
        employeeContribution != null,
        employerContribution != null,
        profitSharingContribution != null
    )
then
    BigDecimal totalContribution = $fact.getEmployeeContribution().add($fact.getEmployerContribution()).add($fact.getProfitSharingContribution());
    $fact.setTotalContribution(totalContribution);
    $fact.setCalculationReason($fact.getCalculationReason() + "; Total: " + totalContribution);
end

// Rule 7: Validate Employee Eligibility
rule "Validate Employee Eligibility"
when
    $fact : PrePayrollCalculationFact(
        employmentStatus != "ACTIVE"
    )
then
    $fact.setEligible(false);
    $fact.setCalculationReason("Not eligible: Employment status is " + $fact.getEmploymentStatus());
end

// Rule 8: Apply Service Requirements
rule "Apply Service Requirements"
when
    $fact : PrePayrollCalculationFact(
        monthsOfService != null,
        minimumServiceMonths != null,
        monthsOfService < minimumServiceMonths
    )
then
    $fact.setEligible(false);
    $fact.setCalculationReason("Not eligible: Service duration " + $fact.getMonthsOfService() + " months (minimum " + $fact.getMinimumServiceMonths() + " required)");
end

// Rule 9: Apply Age Requirements
rule "Apply Age Requirements"
when
    $fact : PrePayrollCalculationFact(
        age != null,
        minimumAge != null,
        age < minimumAge
    )
then
    $fact.setEligible(false);
    $fact.setCalculationReason("Not eligible: Age " + $fact.getAge() + " (minimum " + $fact.getMinimumAge() + " required)");
end

// Rule 10: Mark as Eligible if All Criteria Met
rule "Mark as Eligible"
when
    $fact : PrePayrollCalculationFact(
        employmentStatus == "ACTIVE",
        monthsOfService != null,
        minimumServiceMonths != null,
        age != null,
        minimumAge != null,
        monthsOfService >= minimumServiceMonths,
        age >= minimumAge,
        eligible == false
    )
then
    $fact.setEligible(true);
    $fact.setCalculationReason($fact.getCalculationReason() + "; Eligible: All criteria met");
end
',
NOW()
); 