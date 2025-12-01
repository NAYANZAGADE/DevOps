package com.glidingpath.platform.sponsor.dto;

import lombok.Data;

@Data
public class EmployeeContributionConfigDTO {
    private Boolean hasEmployeeContribution;
    private Double defaultContributionRate;
    private Boolean isAutoEnrollment;
    private Double enrollmentStartRate;
    private Double enrollmentAnnualIncrease;
    private Double enrollmentMaxRate;
    private Double enrollmentMaxContributionRate;
} 