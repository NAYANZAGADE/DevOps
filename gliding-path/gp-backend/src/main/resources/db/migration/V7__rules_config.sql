-- Create rules_config table for dynamic Drools rules
CREATE TABLE IF NOT EXISTS rules_config (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rule_content TEXT NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial eligibility rules using Company Plan configuration
INSERT INTO rules_config (name, rule_content, last_updated)
VALUES (
  'eligibility',
  $$
  package rules

  import com.glidingpath.common.dto.EmployeeEligibilityDTO;
  import com.glidingpath.core.entity.PlanEligibility;
  import java.time.LocalDate;

  // Global variable to hold company plan eligibility configuration
  global PlanEligibility planEligibility;

  // Rule 1: Age Eligibility (uses company plan minimum entry age)
  rule "Age Eligibility Rule"
    salience 0
  when
      $employee: EmployeeEligibilityDTO(
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

  // Rule 6: Re-hire Break in Service Rule (if break > 1 year, restart service)
  rule "Re-hire Break in Service Rule"
  when
      $employee: EmployeeEligibilityDTO(
          rehireDate != null,
          hireDate != null,
          rehireDate.isAfter(hireDate.plusYears(1))
      )
  then
      // Service duration will be calculated from rehire date
      System.out.println("Employee " + $employee.getEmployeeId() + " has break in service, using rehire date for eligibility");
  end
  $$,
  CURRENT_TIMESTAMP
); 