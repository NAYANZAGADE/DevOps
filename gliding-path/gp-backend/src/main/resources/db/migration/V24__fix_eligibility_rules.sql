-- Migration: V23__fix_eligibility_rules.sql
-- Fix eligibility rules by removing old ones and inserting correct one

CREATE TABLE IF NOT EXISTS rules_config (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rule_content TEXT NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Delete all existing eligibility rules
DELETE FROM rules_config WHERE name IN ('eligibility', 'eligibility-v2');

-- Insert the correct eligibility rule
INSERT INTO rules_config (name, rule_content, last_updated)
VALUES ('eligibility', $RULES$
package rules

import com.glidingpath.common.dto.EmployeeEligibilityDTO;
import com.glidingpath.core.entity.PlanEligibility;
import java.time.LocalDate;

global PlanEligibility planEligibility;

// Rule 1: Age Eligibility (uses company plan minimum entry age)
rule "Age Eligibility Rule"
  salience 0
when
    $employee: EmployeeEligibilityDTO(
        planEligibility.getMinimumEntryAge() != null,
        age < planEligibility.getMinimumEntryAge(),
        currentlyEligible == false
    )
then
    $employee.setCurrentlyEligible(false);
    $employee.setEligibilityReason("Not eligible: Age " + $employee.getAge() + " (minimum " + planEligibility.getMinimumEntryAge() + " required)");
    System.out.println("Employee " + $employee.getEmployeeId() + " is not eligible due to age: " + $employee.getAge());
end

// Rule 2: Service Duration Eligibility (uses company plan time employed months)
rule "Service Duration Rule"
  salience 0
when
    $employee: EmployeeEligibilityDTO(
        planEligibility.getTimeEmployedMonths() != null,
        monthsOfService < planEligibility.getTimeEmployedMonths(),
        currentlyEligible == false
    )
then
    $employee.setCurrentlyEligible(false);
    $employee.setEligibilityReason("Not eligible: Service duration " + $employee.getMonthsOfService() + " months (minimum " + planEligibility.getTimeEmployedMonths() + " months required)");
    System.out.println("Employee " + $employee.getEmployeeId() + " is not eligible due to service duration: " + $employee.getMonthsOfService() + " months");
end

// Rule 3: Employment Status Eligibility (must be ACTIVE)
rule "Employment Status Rule"
  salience 0
when
    $employee: EmployeeEligibilityDTO(
        employmentStatus != "ACTIVE",
        currentlyEligible == false
    )
then
    $employee.setCurrentlyEligible(false);
    $employee.setEligibilityReason("Not eligible: Employment status " + $employee.getEmploymentStatus() + " (must be ACTIVE)");
    System.out.println("Employee " + $employee.getEmployeeId() + " is not eligible due to employment status: " + $employee.getEmploymentStatus());
end

// Rule 4: Employment Type Eligibility (must be FULL_TIME)
rule "Employment Type Rule"
  salience 0
when
    $employee: EmployeeEligibilityDTO(
        employmentType != "FULL_TIME",
        currentlyEligible == false
    )
then
    $employee.setCurrentlyEligible(false);
    $employee.setEligibilityReason("Not eligible: Employment type " + $employee.getEmploymentType() + " (must be FULL_TIME)");
    System.out.println("Employee " + $employee.getEmployeeId() + " is not eligible due to employment type: " + $employee.getEmploymentType());
end

// Rule 4.1: Mark employee as eligible if all criteria are met
rule "Mark Eligible"
  salience 100
when
    $employee: EmployeeEligibilityDTO(
        planEligibility.getMinimumEntryAge() != null,
        planEligibility.getTimeEmployedMonths() != null,
        age >= planEligibility.getMinimumEntryAge(),
        monthsOfService >= planEligibility.getTimeEmployedMonths(),
        employmentStatus == "ACTIVE",
        employmentType == "FULL_TIME",
        currentlyEligible == false
    )
then
    $employee.setCurrentlyEligible(true);
    $employee.setEligibilityReason("Eligible: All criteria met (age: " + $employee.getAge() + ", service: " + $employee.getMonthsOfService() + " months)");
    System.out.println("Employee " + $employee.getEmployeeId() + " is eligible.");
end

// Rule 5: Set Eligibility Date for Newly Eligible Employees
rule "Set Eligibility Date Rule"
when
    $employee: EmployeeEligibilityDTO(
        currentlyEligible == true,
        eligibilityDate == null
    )
then
    $employee.setEligibilityDate($employee.getCurrentDate());
    $employee.setEligibilityReason("Eligible: All criteria met (age: " + $employee.getAge() + "+, service: " + $employee.getMonthsOfService() + "+ months)");
    System.out.println("Employee " + $employee.getEmployeeId() + " is eligible as of: " + $employee.getEligibilityDate());
end
$RULES$, CURRENT_TIMESTAMP);
