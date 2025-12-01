package com.glidingpath.platform.sponsor.dto;

import lombok.Data;

@Data
public class ProfitSharingConfigDTO {
    private Boolean isEnabled;
    private String defaultContribution;
    private Double proRataPercentage;
    private Double flatDollarAmount;
    private String comparabilityFormula;
    private String vestingScheduleId;
    private String tenantVestingScheduleId;
} 