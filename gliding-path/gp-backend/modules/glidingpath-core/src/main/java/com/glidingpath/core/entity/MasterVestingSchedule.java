package com.glidingpath.core.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "master_vesting_schedules")
@Data
@EqualsAndHashCode(callSuper = true)
public class MasterVestingSchedule extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "years_to_full_vest")
    private Integer yearsToFullVest;

    private String description;

    @Column(name = "is_system_default")
    private Boolean isSystemDefault = true;

    public enum ScheduleType {
        immediate, graded, cliff
    }
}