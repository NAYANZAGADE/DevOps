package com.glidingpath.platform.sponsor.service.impl;

import com.glidingpath.platform.sponsor.dto.CompanyDetailsDTO;
import com.glidingpath.core.entity.CompanyDetailsEntity;
import com.glidingpath.core.repository.CompanyDetailsRepository;
import com.glidingpath.platform.sponsor.service.CompanyDetailsService;
 

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyDetailsServiceImpl implements CompanyDetailsService {

    private final CompanyDetailsRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public CompanyDetailsDTO saveCompanyDetails(String tenantId, CompanyDetailsDTO dto) {
        Optional<CompanyDetailsEntity> existingOpt = repository.findByTenantId(tenantId);
        if (existingOpt.isPresent()) {
            return updateCompanyDetails(existingOpt, dto);
        }
        CompanyDetailsEntity entity = modelMapper.map(dto, CompanyDetailsEntity.class);
        entity.setId(null);
        entity.setTenantId(tenantId);
        CompanyDetailsEntity savedEntity = repository.save(entity);
        return modelMapper.map(savedEntity, CompanyDetailsDTO.class);
    }

    @Override
    public CompanyDetailsDTO getCompanyDetails(String tenantId) {
        Optional<CompanyDetailsEntity> company = repository.findByTenantId(tenantId);
        if (company.isEmpty()) {
            throw new RuntimeException("Company not found for tenant:"+tenantId);
        }
        return modelMapper.map(company.get(), CompanyDetailsDTO.class);
    }

    @Override
    @Transactional
    public CompanyDetailsDTO updateCompanyDetails(Optional<CompanyDetailsEntity> existingOpt, CompanyDetailsDTO dto) {
        CompanyDetailsEntity existing = existingOpt.get();
        CompanyDetailsEntity updates = modelMapper.map(dto, CompanyDetailsEntity.class);

        // Apply selective updates (only overwrite fields provided in DTO)
        if (updates.getLegalName() != null) existing.setLegalName(updates.getLegalName());
        if (updates.getEin() != null) existing.setEin(updates.getEin());
        if (updates.getEmail() != null) existing.setEmail(updates.getEmail());
        if (updates.getBusinessAddress() != null) existing.setBusinessAddress(updates.getBusinessAddress());
        if (updates.getEntityType() != null) existing.setEntityType(updates.getEntityType());
        if (updates.getPayrollProvider() != null) existing.setPayrollProvider(updates.getPayrollProvider());
        if (updates.getPayrollSchedule() != null) existing.setPayrollSchedule(updates.getPayrollSchedule());

        if (dto.getEstimatedEmployeeCount() != 0) existing.setEstimatedEmployeeCount(dto.getEstimatedEmployeeCount());
        existing.setUnionEmployees(dto.isUnionEmployees());
        existing.setLeasedEmployees(dto.isLeasedEmployees());
        existing.setExistingRetirementPlan(dto.isExistingRetirementPlan());
        existing.setRelatedEntities(dto.isRelatedEntities());

        if (updates.getEmploymentStatus() != null) existing.setEmploymentStatus(updates.getEmploymentStatus());
        if (updates.getBusinessSize() != null) existing.setBusinessSize(updates.getBusinessSize());
        if (updates.getRetirementPlanPriority() != null) existing.setRetirementPlanPriority(updates.getRetirementPlanPriority());

        existing.setHasExisting401k(dto.isHasExisting401k());
        existing.setHasMultipleBusinesses(dto.isHasMultipleBusinesses());

        CompanyDetailsEntity saved = repository.save(existing);
        return modelMapper.map(saved, CompanyDetailsDTO.class);
    }
}
