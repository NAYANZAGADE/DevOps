package com.glidingpath.core.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class EmployerContributionRule extends BaseEntity {
    private String ruleType;
    private Double basicMatchFirstPercent;
    private Double basicMatchFirstRate;
    private Double basicMatchSecondPercent;
    private Double basicMatchSecondRate;
    private Double flexibleMatchPercent;
    private Double nonElectivePercent;
    private Double matchPercentage;
    private Double matchLimitPercent;

    private UUID vestingScheduleId;          // ✅ FIXED: UUID, not String
    private UUID tenantVestingScheduleId;
}