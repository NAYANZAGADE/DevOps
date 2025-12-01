package com.glidingpath.platform.sponsor.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CompanyPlanRequestDTO {
    private UUID planTypeId;
    private int planYear;
    private LocalDate effectiveDate;
    private UUID tenantVestingScheduleId;
    private PlanEligibilityDTO eligibility;
    private EmployeeContributionConfigDTO employeeContributionConfig;
    private EmployerContributionRuleDTO employerContributionRule;
    private ProfitSharingConfigDTO profitSharingConfig;
} 