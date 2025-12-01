package com.glidingpath.platform.shared.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TenantVestingScheduleDTO {
    private UUID id;
    private String tenantId;
    
    // TenantVestingSchedule specific fields
    private String name;
    private String scheduleType;  // "immediate", "graded", "cliff"
    private Integer yearsToFullVest;
} 