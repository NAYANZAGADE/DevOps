package com.glidingpath.platform.sponsor.service.impl;

import com.glidingpath.core.entity.TenantPlan;
import com.glidingpath.core.entity.PlanEligibility;
import com.glidingpath.core.entity.EmployeeContributionConfig;
import com.glidingpath.core.entity.EmployerContributionRule;
import com.glidingpath.core.entity.ProfitSharingConfig;
import com.glidingpath.core.repository.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.glidingpath.platform.sponsor.dto.PlanEligibilityDTO;
import com.glidingpath.platform.sponsor.dto.EmployeeContributionConfigDTO;
import com.glidingpath.platform.sponsor.dto.EmployerContributionRuleDTO;
import com.glidingpath.platform.sponsor.dto.ProfitSharingConfigDTO;
import com.glidingpath.platform.sponsor.dto.CompanyPlanRequestDTO;
import com.glidingpath.platform.sponsor.dto.CompanyPlanResponseDTO;
import com.glidingpath.platform.sponsor.service.CompanyPlanService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyPlanServiceImpl implements CompanyPlanService {

    private final ModelMapper modelMapper;
    private final TenantPlanRepository tenantPlanRepository;
    
    @Override
    @Transactional
    public CompanyPlanResponseDTO saveCompanyPlan(String tenantId, CompanyPlanRequestDTO request) {
        TenantPlan tenantPlan = new TenantPlan();
        tenantPlan.setTenantId(tenantId);
        tenantPlan.setPlanTypeId(request.getPlanTypeId());
        tenantPlan.setPlanYear(request.getPlanYear());
        tenantPlan.setEffectiveDate(request.getEffectiveDate());

        if (request.getEligibility() != null) {
            PlanEligibility eligibility = new PlanEligibility();
            eligibility.setTenantId(tenantId);
            eligibility.setMinimumEntryAge(request.getEligibility().getMinimumEntryAge());
            eligibility.setTimeEmployedMonths(request.getEligibility().getTimeEmployedMonths());
            if (request.getEligibility().getExclusions() != null) {
                eligibility.setExclusions(request.getEligibility().getExclusions());
            }
            tenantPlan.setEligibility(eligibility);
        }

        if (request.getEmployeeContributionConfig() != null) {
            EmployeeContributionConfig config = new EmployeeContributionConfig();
            config.setTenantId(tenantId);
            config.setHasEmployeeContribution(request.getEmployeeContributionConfig().getHasEmployeeContribution());
            config.setDefaultContributionRate(request.getEmployeeContributionConfig().getDefaultContributionRate());
            config.setIsAutoEnrollment(request.getEmployeeContributionConfig().getIsAutoEnrollment());
            config.setEnrollmentStartRate(request.getEmployeeContributionConfig().getEnrollmentStartRate());
            config.setEnrollmentAnnualIncrease(request.getEmployeeContributionConfig().getEnrollmentAnnualIncrease());
            config.setEnrollmentMaxRate(request.getEmployeeContributionConfig().getEnrollmentMaxRate());
            config.setEnrollmentMaxContributionRate(request.getEmployeeContributionConfig().getEnrollmentMaxContributionRate());
            tenantPlan.setEmployeeContributionConfig(config);
        }

        if (request.getEmployerContributionRule() != null) {
            EmployerContributionRule rule = new EmployerContributionRule();
            rule.setTenantId(tenantId);
            rule.setRuleType(request.getEmployerContributionRule().getRuleType());
            rule.setBasicMatchFirstPercent(request.getEmployerContributionRule().getBasicMatchFirstPercent());
            rule.setBasicMatchFirstRate(request.getEmployerContributionRule().getBasicMatchFirstRate());
            rule.setBasicMatchSecondPercent(request.getEmployerContributionRule().getBasicMatchSecondPercent());
            rule.setBasicMatchSecondRate(request.getEmployerContributionRule().getBasicMatchSecondRate());
            rule.setFlexibleMatchPercent(request.getEmployerContributionRule().getFlexibleMatchPercent());
            rule.setNonElectivePercent(request.getEmployerContributionRule().getNonElectivePercent());
            rule.setMatchPercentage(request.getEmployerContributionRule().getMatchPercentage());
            rule.setMatchLimitPercent(request.getEmployerContributionRule().getMatchLimitPercent());
            rule.setVestingScheduleId(request.getEmployerContributionRule().getVestingScheduleId() != null ? 
                java.util.UUID.fromString(request.getEmployerContributionRule().getVestingScheduleId()) : null);
            rule.setTenantVestingScheduleId(request.getEmployerContributionRule().getTenantVestingScheduleId() != null ? 
                java.util.UUID.fromString(request.getEmployerContributionRule().getTenantVestingScheduleId()) : null);
            tenantPlan.setEmployerContributionRule(rule);
        }

        if (request.getProfitSharingConfig() != null) {
            ProfitSharingConfig config = new ProfitSharingConfig();
            config.setTenantId(tenantId);
            config.setIsEnabled(request.getProfitSharingConfig().getIsEnabled());
            config.setDefaultContribution(request.getProfitSharingConfig().getDefaultContribution());
            config.setProRataPercentage(request.getProfitSharingConfig().getProRataPercentage());
            config.setFlatDollarAmount(request.getProfitSharingConfig().getFlatDollarAmount());
            config.setComparabilityFormula(request.getProfitSharingConfig().getComparabilityFormula());
            config.setVestingScheduleId(request.getProfitSharingConfig().getVestingScheduleId() != null ? 
                java.util.UUID.fromString(request.getProfitSharingConfig().getVestingScheduleId()) : null);
            config.setTenantVestingScheduleId(request.getProfitSharingConfig().getTenantVestingScheduleId() != null ? 
                java.util.UUID.fromString(request.getProfitSharingConfig().getTenantVestingScheduleId()) : null);
            tenantPlan.setProfitSharingConfig(config);
        }

        TenantPlan saved = tenantPlanRepository.save(tenantPlan);
        CompanyPlanResponseDTO response = new CompanyPlanResponseDTO();
        
        response.setId(saved.getId());
        response.setTenantId(saved.getTenantId());
        response.setPlanTypeId(saved.getPlanTypeId());
        response.setPlanYear(saved.getPlanYear());
        response.setEffectiveDate(saved.getEffectiveDate());
        
        if (saved.getEligibility() != null) {
            response.setEligibility(modelMapper.map(saved.getEligibility(), PlanEligibilityDTO.class));
        }
        if (saved.getEmployeeContributionConfig() != null) {
            response.setEmployeeContributionConfig(modelMapper.map(saved.getEmployeeContributionConfig(), EmployeeContributionConfigDTO.class));
        }
        if (saved.getEmployerContributionRule() != null) {
            response.setEmployerContributionRule(modelMapper.map(saved.getEmployerContributionRule(), EmployerContributionRuleDTO.class));
        }
        if (saved.getProfitSharingConfig() != null) {
            response.setProfitSharingConfig(modelMapper.map(saved.getProfitSharingConfig(), ProfitSharingConfigDTO.class));
        }
        return response;
    }

    @Override
    public List<CompanyPlanResponseDTO> getPlansByTenantId(String tenantId) {
        return tenantPlanRepository.findByTenantId(tenantId).stream()
                .map(plan -> {
                    CompanyPlanResponseDTO dto = modelMapper.map(plan, CompanyPlanResponseDTO.class);
                    dto.setId(plan.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

