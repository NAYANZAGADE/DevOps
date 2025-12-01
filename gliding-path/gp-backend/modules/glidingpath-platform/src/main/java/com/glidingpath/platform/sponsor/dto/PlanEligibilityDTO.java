package com.glidingpath.platform.sponsor.dto;

import java.util.Set;
import com.glidingpath.core.enums.ExclusionType;
import lombok.Data;

@Data
public class PlanEligibilityDTO {
	private Integer minimumEntryAge;
	private Integer timeEmployedMonths;
	private Set<ExclusionType> exclusions;
}