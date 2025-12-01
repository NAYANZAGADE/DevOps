package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tenant_vesting_schedules")
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantVestingSchedule extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "schedule_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Column(name = "years_to_full_vest")
    private Integer yearsToFullVest;

    public enum ScheduleType {
        immediate, graded, cliff
    }
}