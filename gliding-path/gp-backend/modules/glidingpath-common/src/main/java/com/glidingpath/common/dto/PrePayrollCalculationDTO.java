package com.glidingpath.common.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrePayrollCalculationDTO {
    
    // Request fields
    private String tenantId;
    private String employeeId;
    private LocalDate payrollPeriodStart;
    private LocalDate payrollPeriodEnd;
    
    // Response fields
    private String calculationId;
    private LocalDateTime calculationDate;
    private String status;
    private String errorMessage;
    
    // Employee contribution calculations
    private BigDecimal employeeContributionAmount;
    private BigDecimal employeeContributionPercentage;
    
    // Employer match calculations
    private BigDecimal employerMatchAmount;
    private BigDecimal employerMatchPercentage;
    
    // Profit sharing calculations
    private BigDecimal profitSharingAmount;
    private BigDecimal profitSharingPercentage;
    
    // Total calculations
    private BigDecimal totalContributionAmount;
    private BigDecimal totalContributionPercentage;
    
    // Base salary for calculations
    private BigDecimal baseSalary;
    private BigDecimal eligibleCompensation;
    
    // Plan configuration references
    private UUID planId;
    private UUID employerContributionRuleId;
    private UUID employeeContributionConfigId;
    private UUID profitSharingConfigId;
    
    // Processing metadata
    private LocalDateTime processedAt;
    private Integer reprocessedCount;
    private LocalDateTime lastReprocessedAt;
    
    // Finch integration fields
    private String finchStatus; // "CREATED", "FAILED", "PENDING", "NO_DEDUCTIONS"
    private String finchErrorMessage;
    private String finchBenefitId; // Store the Finch benefit ID for employee contribution
    private String finchEmployerBenefitId; // Store the Finch benefit ID for employer match
    private String finchProfitSharingBenefitId; // Store the Finch benefit ID for profit sharing
    private LocalDateTime finchProcessedAt; // When Finch processing was completed
} 