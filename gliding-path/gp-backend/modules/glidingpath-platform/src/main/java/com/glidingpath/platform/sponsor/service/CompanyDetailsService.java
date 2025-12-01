package com.glidingpath.platform.sponsor.service;

import java.util.Optional;

import com.glidingpath.core.entity.CompanyDetailsEntity;
import com.glidingpath.platform.sponsor.dto.CompanyDetailsDTO;

public interface CompanyDetailsService {
    CompanyDetailsDTO saveCompanyDetails(String tenantId, CompanyDetailsDTO dto);
    CompanyDetailsDTO getCompanyDetails(String tenantId);
    CompanyDetailsDTO updateCompanyDetails(Optional<CompanyDetailsEntity> existingOpt,CompanyDetailsDTO dto);
} 