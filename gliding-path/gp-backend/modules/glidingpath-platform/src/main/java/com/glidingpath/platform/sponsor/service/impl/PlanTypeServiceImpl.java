package com.glidingpath.platform.sponsor.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.glidingpath.platform.sponsor.dto.PlanTypeDTO;
import com.glidingpath.core.entity.PlanType;
import com.glidingpath.core.entity.PlanTypeFeature;
import com.glidingpath.core.repository.PlanTypeRepository;
import com.glidingpath.platform.sponsor.service.PlanTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanTypeServiceImpl implements PlanTypeService {

    private final PlanTypeRepository planTypeRepository;
    @Override
    public List<PlanTypeDTO> getAllPlanTypes() {
        return planTypeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private PlanTypeDTO convertToDto(PlanType planType) {
        PlanTypeDTO dto = new PlanTypeDTO();
        dto.setId(planType.getId());
        dto.setName(planType.getName());
        dto.setDescription(planType.getDescription());
        dto.setMonthlyCost(planType.getMonthlyCost());
        dto.setPerParticipantFee(planType.getPerParticipantFee());
        dto.setEmployerAccountFee(planType.getEmployerAccountFee());
        dto.setEmployeeAccountFee(planType.getEmployeeAccountFee());
        dto.setEmployerContribution(planType.getEmployerContribution());
        dto.setEmployeeContributionLimit(planType.getEmployeeContributionLimit());
        dto.setComplianceProtection(planType.getComplianceProtection());
        dto.setTaxCredit(planType.getTaxCredit());
		dto.setHeadline(planType.getHeadline());
		dto.setLongDescription(planType.getLongDescription());
		// features via relationship
		List<String> features = planType.getFeatures().stream()
				.map(PlanTypeFeature::getLabel)
				.collect(Collectors.toList());
		dto.setFeatures(features);
		return dto;
    }

}