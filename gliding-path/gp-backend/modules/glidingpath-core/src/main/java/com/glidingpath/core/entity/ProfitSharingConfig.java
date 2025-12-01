package com.glidingpath.core.entity;


import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfitSharingConfig extends BaseEntity {
    private Boolean isEnabled;
    private String defaultContribution;
    private Double proRataPercentage;
    private Double flatDollarAmount;
    private String comparabilityFormula;
    private UUID vestingScheduleId;
    private UUID tenantVestingScheduleId;
}
