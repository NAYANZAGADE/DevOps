package com.glidingpath.platform.sponsor.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.glidingpath.platform.sponsor.dto.EmployeeContributionConfigDTO;
import com.glidingpath.platform.sponsor.dto.EmployerContributionRuleDTO;
import com.glidingpath.platform.sponsor.dto.PlanEligibilityDTO;
import com.glidingpath.platform.sponsor.dto.ProfitSharingConfigDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPlanResponseDTO {
    private UUID id;
    private String tenantId;
    private UUID planTypeId;
    private int planYear;
    private LocalDate effectiveDate;
    private UUID tenantVestingScheduleId;
    private PlanEligibilityDTO eligibility;
    private EmployeeContributionConfigDTO employeeContributionConfig;
    private EmployerContributionRuleDTO employerContributionRule;
    private ProfitSharingConfigDTO profitSharingConfig;
} 