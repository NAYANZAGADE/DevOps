package com.glidingpath.core.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantPlan extends BaseEntity {
    private UUID planTypeId;
    private int planYear;
    private LocalDate effectiveDate;
    private UUID tenantVestingScheduleId;

    @OneToOne(cascade = CascadeType.ALL)
    private PlanEligibility eligibility;

    @OneToOne(cascade = CascadeType.ALL)
    private EmployeeContributionConfig employeeContributionConfig;

    @OneToOne(cascade = CascadeType.ALL)
    private EmployerContributionRule employerContributionRule;

    @OneToOne(cascade = CascadeType.ALL)
    private ProfitSharingConfig profitSharingConfig;
}
