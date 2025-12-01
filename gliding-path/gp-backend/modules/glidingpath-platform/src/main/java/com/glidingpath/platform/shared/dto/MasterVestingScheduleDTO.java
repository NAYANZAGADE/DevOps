package com.glidingpath.platform.shared.dto;

import lombok.Data;
import java.util.UUID;

import com.glidingpath.core.entity.MasterVestingSchedule.ScheduleType;

@Data
public class MasterVestingScheduleDTO {
    private UUID id;
    private String tenantId;
    private String name;
    private ScheduleType scheduleType; 
    private Integer yearsToFullVest;
    private String description;
    private Boolean isSystemDefault;
} 