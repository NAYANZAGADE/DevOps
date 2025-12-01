package com.glidingpath.platform.shared.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Clean PrePayrollCalculationFact for Drools rule processing
 * 
 * All monetary values use BigDecimal for precision.
 * No legacy field compatibility - clean, focused structure.
 */
@Data
public class PrePayrollCalculationFact implements Serializable {
    
    // Employee identification
    private String employeeId;
    
    // Compensation data
    private BigDecimal employeeAnnualCompensation;
    private BigDecimal eligibleCompensation;
    private BigDecimal compensationLimit;
    
    // Employee contribution data
    private BigDecimal employeeContributionPercent;
    private BigDecimal employeeContribution;
    private BigDecimal maxContributionLimit;
    private BigDecimal minContributionRequired;
    
    // Employer match data
    private BigDecimal employerMatchPercent;
    private BigDecimal employerContribution;
    private String employerMatchType; // "BASIC_MATCH" or "PERCENTAGE_MATCH"
    
    // Auto-enrollment data
    private Boolean autoEnrollmentEnabled;
    private BigDecimal autoEnrollmentPercent;
    
    // Profit sharing data
    private BigDecimal profitSharingPercent;
    private BigDecimal profitSharingContribution;
    
    // Total calculations
    private BigDecimal totalContribution;
    
    // Eligibility data
    private Boolean eligible;
    private String eligibilityReason;
    
    // Calculation tracking
    private String calculationReason;
    
    // Additional fields for complex rules
    private Integer age;
    private Integer monthsOfService;
    private Integer minimumServiceMonths;
    private Integer minimumAge;
    private String employmentStatus;
    private String planType;
    private Boolean isNewHire;
    private Boolean isTerminated;
} 