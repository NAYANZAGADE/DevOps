package com.glidingpath.core.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeContributionConfig extends BaseEntity {
    private Boolean hasEmployeeContribution;
    private Double defaultContributionRate;
    private Boolean isAutoEnrollment;
    private Double enrollmentStartRate;
    private Double enrollmentAnnualIncrease;
    private Double enrollmentMaxRate;
    private Double enrollmentMaxContributionRate;
}