package com.glidingpath.platform.sponsor.service;

import com.glidingpath.platform.sponsor.dto.CompanyPlanRequestDTO;
import com.glidingpath.platform.sponsor.dto.CompanyPlanResponseDTO;

import java.util.List;

public interface CompanyPlanService {
    CompanyPlanResponseDTO saveCompanyPlan(String tenantId, CompanyPlanRequestDTO request);
    List<CompanyPlanResponseDTO> getPlansByTenantId(String tenantId);
} 