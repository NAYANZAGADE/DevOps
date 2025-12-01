package com.glidingpath.platform.sponsor.service;

import com.glidingpath.platform.sponsor.dto.PlanStartDateDTO;

import java.util.Optional;
import java.util.UUID;

public interface PlanStartDateService {
	void savePlanStartDate(PlanStartDateDTO dto);
	Optional<PlanStartDateDTO> getByTenantPlanId(UUID tenantPlanId);
} 