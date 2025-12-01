package com.glidingpath.platform.sponsor.service.impl;


import com.glidingpath.platform.sponsor.dto.PlanStartDateDTO;
import com.glidingpath.platform.sponsor.service.PlanStartDateService;
import com.glidingpath.core.entity.PlanStartDate;
import com.glidingpath.core.repository.PlanStartDateRepository;
import com.glidingpath.core.repository.TenantPlanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of PlanStartDateService using ObjectMapper for mapping.
 */
@Service
@RequiredArgsConstructor
public class PlanStartDateServiceImpl implements PlanStartDateService {

    private final PlanStartDateRepository repository;
    private final ObjectMapper objectMapper;
    private final TenantPlanRepository tenantPlanRepository;

    @Override
    public void savePlanStartDate(PlanStartDateDTO dto) {
        PlanStartDate entity = objectMapper.convertValue(dto, PlanStartDate.class);
        if (dto.getTenantPlanId() != null) {
            tenantPlanRepository.findById(dto.getTenantPlanId()).ifPresent(entity::setTenantPlan);
        }
        repository.save(entity);
    }

    private PlanStartDateDTO toDto(PlanStartDate entity) {
        PlanStartDateDTO dto = objectMapper.convertValue(entity, PlanStartDateDTO.class);
        if (entity.getTenantPlan() != null) {
            dto.setTenantPlanId(entity.getTenantPlan().getId());
        }
        return dto;
    }

    @Override
    public Optional<PlanStartDateDTO> getByTenantPlanId(UUID tenantPlanId) {
        return repository.findAll().stream()
                .filter(psd -> psd.getTenantPlan() != null && tenantPlanId.equals(psd.getTenantPlan().getId()))
                .findFirst()
                .map(this::toDto);
    }
}
