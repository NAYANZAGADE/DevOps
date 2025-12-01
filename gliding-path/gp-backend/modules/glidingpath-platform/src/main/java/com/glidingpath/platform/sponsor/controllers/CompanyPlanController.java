package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.platform.sponsor.dto.CompanyPlanRequestDTO;
import com.glidingpath.platform.sponsor.dto.CompanyPlanResponseDTO;
import com.glidingpath.platform.sponsor.service.CompanyPlanService;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plan-sponsor/plan")
@RequiredArgsConstructor
@Tag(name = "Plan-Sponsor Details", description = "APIs for creating and managing company plan configurations with eligibility rules and contribution settings")
public class CompanyPlanController {

    private final CompanyPlanService companyPlanService;
    private final OnboardingCompanyStateService onboardingService;

    /**
     * Endpoint to create a new company plan configuration.
     *
     * @param request CompanyPlanRequestDTO containing plan configuration details
     * @return ResponseEntity with CompanyPlanResponseDTO containing created plan information
     */
    @PostMapping
    @Operation(summary = "Create company plan configuration", description = "Creates comprehensive company plan with eligibility rules, contribution limits, and vesting schedules")
    public ResponseEntity<CompanyPlanResponseDTO> create(@CurrentUser User user, @RequestBody CompanyPlanRequestDTO request) {
        String tenantId = user.getTenantId();
        CompanyPlanResponseDTO response = companyPlanService.saveCompanyPlan(tenantId, request);
        onboardingService.updateState(tenantId, OnBoardingState.PLAN_DETAILS);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve all plan configurations for a specific tenant.
     *
     * @param tenantId the tenant identifier
     * @return ResponseEntity with List of CompanyPlanResponseDTO containing plan configurations
     */
    @GetMapping()
    @Operation(summary = "Get all plan configurations for tenant", description = "Retrieves all company plan configurations including eligibility rules and contribution settings")
    public ResponseEntity<List<CompanyPlanResponseDTO>> getByTenant(@CurrentUser User user) {
        String tenantId = user.getTenantId();
        return ResponseEntity.ok(companyPlanService.getPlansByTenantId(tenantId));
    }
} 