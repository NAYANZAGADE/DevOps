package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.platform.sponsor.dto.PlanStartDateDTO;
import com.glidingpath.platform.sponsor.service.PlanStartDateService;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;
import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * REST controller for handling plan start date creation.
 */
@RestController
@RequestMapping("/plan-sponsor/plan")
@RequiredArgsConstructor
@Tag(name = "Plan-Sponsor Details", description = "APIs for managing retirement plan start dates and important milestone dates")
public class PlanStartDateController {

    private final PlanStartDateService planStartDateService;
    private final OnboardingCompanyStateService onboardingService;

    /**
     * Endpoint to save plan start date and important related dates.
     *
     * @param tenantId the tenant identifier
     * @param dto PlanStartDateDTO containing the user's inputs
     * @return ResponseEntity with String confirmation message
     */
    @PostMapping("/start-date")
    @Operation(summary = "Save plan start date", description = "Saves plan start date and important milestone dates for retirement plan")
    public ResponseEntity<String> savePlanStartDate(@CurrentUser User user, @RequestBody PlanStartDateDTO dto) {
        planStartDateService.savePlanStartDate(dto);
        String tenantId = user.getTenantId();
        onboardingService.updateState(tenantId, OnBoardingState.PLAN_START_DATE);
        return ResponseEntity.ok("Plan start date saved successfully.");
    }

    /**
     * Endpoint to get plan start date and important dates for a given tenantPlanId.
     *
     * @param tenantPlanId the ID of the tenant plan
     * @return ResponseEntity with PlanStartDateDTO containing important dates
     */
    @GetMapping("/start-date/{tenantPlanId}")
    public ResponseEntity<PlanStartDateDTO> getPlanStartDate(@PathVariable("tenantPlanId") UUID tenantPlanId) {
        return ResponseEntity.of(planStartDateService.getByTenantPlanId(tenantPlanId));
    }
} 