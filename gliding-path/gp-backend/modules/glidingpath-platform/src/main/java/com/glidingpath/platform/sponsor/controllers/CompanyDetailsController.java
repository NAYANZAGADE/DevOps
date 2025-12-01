package com.glidingpath.platform.sponsor.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glidingpath.platform.sponsor.dto.CompanyDetailsDTO;
import com.glidingpath.platform.sponsor.service.CompanyDetailsService;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;
import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.core.entity.User;
import com.glidingpath.auth.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * REST controller for managing company details during onboarding process.
 * Provides endpoints for saving and retrieving company information and
 * managing onboarding state transitions.
 */
@RestController
@RequestMapping("/plan-sponsor/details")
@RequiredArgsConstructor
@Tag(name = "Plan-Sponsor Details", description = "APIs for managing business details")
public class CompanyDetailsController {
    private final CompanyDetailsService service;
    private final OnboardingCompanyStateService onboardingService;
    /**
     * Endpoint to save company details and update onboarding state.
     *
     * @param tenantId the tenant identifier
     * @param dto CompanyDetailsDTO containing company information
     * @return ResponseEntity with CompanyDetailsDTO containing saved company details
     */
    @PostMapping()
    @Operation(summary = "Save company business details", description = "Stores business information")
    public ResponseEntity<CompanyDetailsDTO> saveDetails(@CurrentUser User user,@RequestBody CompanyDetailsDTO dto) {
    	String tenantId = user.getTenantId();
        CompanyDetailsDTO savedDetails = service.saveCompanyDetails(tenantId, dto);
        onboardingService.updateState(tenantId, OnBoardingState.BUISNESS_DETAILS);
        return ResponseEntity.ok(savedDetails);
    }

    /**
     * Endpoint to retrieve company details by ID.
     *
     * @param id the company details identifier
     * @return ResponseEntity with CompanyDetailsDTO containing company information
     */
    @GetMapping()
    public ResponseEntity<CompanyDetailsDTO> getDetails(@CurrentUser User user) {
    	String tenantId = user.getTenantId();
        return ResponseEntity.ok(service.getCompanyDetails(tenantId));
    }
} 