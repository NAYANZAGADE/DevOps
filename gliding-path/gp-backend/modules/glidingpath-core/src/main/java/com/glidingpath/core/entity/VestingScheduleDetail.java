package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "vesting_schedule_details")
@Data
@EqualsAndHashCode(callSuper = true)
public class VestingScheduleDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vesting_schedule_id")
    private MasterVestingSchedule vestingSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_vesting_schedule_id")
    private TenantVestingSchedule tenantVestingSchedule;

    @Column(name = "years_of_service", nullable = false)
    private Integer yearsOfService;

    @Column(name = "vested_percentage", nullable = false)
    private Double vestedPercentage;
}