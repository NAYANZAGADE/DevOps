package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.platform.shared.dto.TrusteeConfirmationRequestDTO;
import com.glidingpath.platform.shared.dto.PlanSignatureRequestDTO;
import com.glidingpath.platform.shared.dto.TrusteeConfirmationResponseDTO;
import com.glidingpath.platform.sponsor.service.TrusteeSignatureService;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;
import com.glidingpath.platform.shared.dto.PlanSignatureResponseDTO;
import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trustee")
@RequiredArgsConstructor
@Tag(name = "Trustee Signature Management", description = "APIs for managing trustee confirmations and plan signature operations for retirement plan compliance")
public class TrusteeSignatureController {
  
    private final TrusteeSignatureService trusteeSignatureService;
    private final OnboardingCompanyStateService onboardingService;

    /**
     * Endpoint to create a trustee confirmation record.
     *
     * @param request TrusteeConfirmationRequestDTO containing confirmation details
     * @return ResponseEntity with TrusteeConfirmationResponseDTO containing confirmation information
     */
    @PostMapping("/confirmation")
    @Operation(summary = "Create trustee confirmation", description = "Creates trustee confirmation record for retirement plan compliance")
    public ResponseEntity<TrusteeConfirmationResponseDTO> createTrusteeConfirmation(@CurrentUser User user, @Valid @RequestBody TrusteeConfirmationRequestDTO request) {
        String tenantId = user.getTenantId();
        TrusteeConfirmationResponseDTO response = trusteeSignatureService.createTrusteeConfirmation(tenantId, request);
        onboardingService.updateState(tenantId, OnBoardingState.TRUSTEE);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to create a plan signature.
     * This endpoint handles the plan document review and signature process.
     * @param request PlanSignatureRequestDTO containing signature details
     * @return ResponseEntity with PlanSignatureResponseDTO containing signature information
     */
    @PostMapping("/signature")
    @Operation(summary = "Create plan signature", description = "Creates plan signature record for trustee and advances onboarding to signature completion")
    public ResponseEntity<PlanSignatureResponseDTO> createPlanSignature(@CurrentUser User user, @Valid @RequestBody PlanSignatureRequestDTO request) {
        String tenantId = user.getTenantId();
        PlanSignatureResponseDTO response = trusteeSignatureService.createPlanSignature(tenantId, request);
        onboardingService.updateState(tenantId, OnBoardingState.SIGN);
        return ResponseEntity.ok(response);
    }
} 