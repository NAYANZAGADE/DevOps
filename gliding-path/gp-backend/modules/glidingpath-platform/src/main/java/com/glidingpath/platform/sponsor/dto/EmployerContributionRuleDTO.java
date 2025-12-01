package com.glidingpath.platform.sponsor.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class EmployerContributionRuleDTO {
    private String ruleType;
    private Double basicMatchFirstPercent;
    private Double basicMatchFirstRate;
    private Double basicMatchSecondPercent;
    private Double basicMatchSecondRate;
    private Double flexibleMatchPercent;
    private Double nonElectivePercent;
    private Double matchPercentage;
    private Double matchLimitPercent;

    private String vestingScheduleId;
    private String tenantVestingScheduleId;
} 