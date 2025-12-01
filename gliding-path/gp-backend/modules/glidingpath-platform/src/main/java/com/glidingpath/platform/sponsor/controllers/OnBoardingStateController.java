package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;
import com.glidingpath.platform.sponsor.dto.OnBoardingCompanyStateResponseDTO;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

/**
 * Controller for managing onboarding state transitions
 * Supports simplified flow with tenant-based state management
 */
@Slf4j
@RestController
@RequestMapping("/plan-sponsor")
@RequiredArgsConstructor
@Validated
@Tag(name = "Plan-Sponsor Details", description = "APIs for tracking and managing company onboarding progress through different stages")
public class OnBoardingStateController {

    private final OnboardingCompanyStateService onboardingService;
    

    @GetMapping("/state")
    @Operation(summary = "Get current onboarding status by tenant",
               description = "Retrieve the current onboarding state for a tenant")
    public ResponseEntity<OnBoardingCompanyStateResponseDTO> getOnBoardingState(@CurrentUser User user) {
    	String tenantId=user.getTenantId();
        OnBoardingCompanyStateResponseDTO response = onboardingService.getStateByTenant(tenantId);
        log.info("Retrieved status: {} for tenantId: {}", response.getCurrentState(), tenantId);
        return ResponseEntity.ok(response);
    }
} 