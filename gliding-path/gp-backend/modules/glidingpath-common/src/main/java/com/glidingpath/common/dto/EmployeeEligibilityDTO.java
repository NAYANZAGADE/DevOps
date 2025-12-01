package com.glidingpath.common.dto;

import java.time.LocalDate;
import java.time.Period;

import lombok.Data;

@Data
public class EmployeeEligibilityDTO {
    // Core employee identification
    private String employeeId;
    private String tenantId;
    // Eligibility evaluation data
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private LocalDate rehireDate;
    private String employmentStatus; // ACTIVE, INACTIVE, TERMINATED
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACTOR
    private LocalDate currentDate;
    
    // Eligibility results (set by Drools)
    private boolean eligible;
    private LocalDate eligibilityDate;
    private String eligibilityReason;
    
    // Public fields for Drools compatibility
    public int age;
    public int monthsOfService;
    
    // Calculated fields for eligibility evaluation
    public int getAge() {
        if (dateOfBirth == null || currentDate == null) return 0;
        return Period.between(dateOfBirth, currentDate).getYears();
    }
    
    public int getMonthsOfService() {
        LocalDate startDate = rehireDate != null ? rehireDate : hireDate;
        if (startDate == null || currentDate == null) return 0;
        Period period = Period.between(startDate, currentDate);
        return period.getYears() * 12 + period.getMonths();
    }
    
    // Methods for Drools rules compatibility
    public boolean getCurrentlyEligible() {
        return eligible;
    }
    
    public void setCurrentlyEligible(boolean currentlyEligible) {
        this.eligible = currentlyEligible;
    }
} 