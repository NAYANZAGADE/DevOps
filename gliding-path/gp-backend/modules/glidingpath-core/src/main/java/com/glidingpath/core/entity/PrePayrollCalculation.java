package com.glidingpath.core.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pre_payroll_calculations")
@Data
@EqualsAndHashCode(callSuper = true)
public class PrePayrollCalculation extends BaseEntity {
    
    @Column(name = "calculation_id", unique = true, nullable = false)
    private String calculationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private PlanParticipant employee;
    
    @Column(name = "payroll_period_start")
    private LocalDate payrollPeriodStart;
    
    @Column(name = "payroll_period_end")
    private LocalDate payrollPeriodEnd;
    
    @Column(name = "calculation_date")
    private LocalDateTime calculationDate;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CalculationStatus status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    // Employee contribution calculations
    @Column(name = "employee_contribution_amount", precision = 15, scale = 2)
    private BigDecimal employeeContributionAmount;
    
    @Column(name = "employee_contribution_percentage", precision = 5, scale = 2)
    private BigDecimal employeeContributionPercentage;
    
    // Employer match calculations
    @Column(name = "employer_match_amount", precision = 15, scale = 2)
    private BigDecimal employerMatchAmount;
    
    @Column(name = "employer_match_percentage", precision = 5, scale = 2)
    private BigDecimal employerMatchPercentage;
    
    // Profit sharing calculations
    @Column(name = "profit_sharing_amount", precision = 15, scale = 2)
    private BigDecimal profitSharingAmount;
    
    @Column(name = "profit_sharing_percentage", precision = 5, scale = 2)
    private BigDecimal profitSharingPercentage;
    
    // Total calculations
    @Column(name = "total_contribution_amount", precision = 15, scale = 2)
    private BigDecimal totalContributionAmount;
    
    @Column(name = "total_contribution_percentage", precision = 5, scale = 2)
    private BigDecimal totalContributionPercentage;
    
    // Base salary for calculations
    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;
    
    @Column(name = "eligible_compensation", precision = 15, scale = 2)
    private BigDecimal eligibleCompensation;
    
    // Plan configuration references
    @Column(name = "plan_id")
    private UUID planId;
    
    @Column(name = "employer_contribution_rule_id")
    private UUID employerContributionRuleId;
    
    @Column(name = "employee_contribution_config_id")
    private UUID employeeContributionConfigId;
    
    @Column(name = "profit_sharing_config_id")
    private UUID profitSharingConfigId;
    
    // Processing metadata
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "reprocessed_count")
    private Integer reprocessedCount = 0;
    
    @Column(name = "last_reprocessed_at")
    private LocalDateTime lastReprocessedAt;
    
    public enum CalculationStatus {
        PENDING,
        IN_PROGRESS,
        SUCCESS,
        FAILED,
        REPROCESSED
    }
} 