package com.glidingpath.platform.shared.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class VestingScheduleDetailDTO {
    private UUID id;
    private String tenantId;
    
    // VestingScheduleDetail specific fields
    private String vestingScheduleId;  // UUID as String for API compatibility
    private String tenantVestingScheduleId;  // UUID as String for API compatibility
    private Integer yearsOfService;
    private Double vestedPercentage;
}