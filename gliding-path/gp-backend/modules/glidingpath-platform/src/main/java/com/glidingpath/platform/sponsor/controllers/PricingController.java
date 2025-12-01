package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.platform.sponsor.dto.CorePricingDTO;
import com.glidingpath.platform.sponsor.service.CorePricingService;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;
import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing")
@Tag(name = "Pricing Management", description = "APIs for retrieving core pricing information of existing plan-types")
public class PricingController {

    private final CorePricingService pricingService;
    private final OnboardingCompanyStateService onboardingService;

    public PricingController(CorePricingService pricingService, OnboardingCompanyStateService onboardingService) {
        this.pricingService = pricingService;
        this.onboardingService = onboardingService;
    }

    /**
     * Endpoint to fetch core pricing data for a specific tenant.
     *
     * @param tenantId the tenant identifier
     * @return ResponseEntity with CorePricingDTO containing pricing information
     */
    @GetMapping()
	public ResponseEntity<CorePricingDTO> getCorePricing(@CurrentUser User user) {
		String tenantId = user.getTenantId();
		CorePricingDTO corePricing = pricingService.getCorePricing(tenantId);
        onboardingService.updateState(tenantId, OnBoardingState.PRICING);
    	return ResponseEntity.ok(corePricing);
    }

} 