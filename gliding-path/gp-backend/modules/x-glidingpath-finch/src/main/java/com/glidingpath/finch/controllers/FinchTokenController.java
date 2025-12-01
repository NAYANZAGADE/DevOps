package com.glidingpath.finch.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.JsonNode;
import com.glidingpath.finch.dto.FinchAuthCodeRequest;
import com.glidingpath.finch.dto.TokenAccessResult;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.core.entity.User;
import com.glidingpath.finch.dto.FinchUrlDTO;
import com.glidingpath.finch.service.FinchService;
import com.glidingpath.finch.service.FinchUrlService;
import com.glidingpath.finch.service.FinchEmployeeBatchService;
import com.glidingpath.finch.service.TokenManager;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;
import com.glidingpath.rules.service.PrePayrollBatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/finch/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Finch OAuth Authentication", description = "APIs for managing Finch OAuth authentication flow and access token management")
public class FinchTokenController {

    private final FinchService finchService;
    private final TokenManager tokenManager;
    private final FinchUrlService finchUrlService;
    private final FinchEmployeeBatchService batchEmployeeService;
    private final OnboardingCompanyStateService onboardingService;
    private final PrePayrollBatchService prePayrollBatchService;

    /**
     * Endpoint to generate Finch OAuth redirect URL.
     *
     * @return ResponseEntity with FinchUrlDTO containing redirect URL for OAuth flow
     */
    @GetMapping("/url")
    public ResponseEntity<FinchUrlDTO> getFinchRedirectUrl(@CurrentUser User user) {
        String tenantId = user.getTenantId();
        FinchUrlDTO response = finchUrlService.generateFinchConnectUrl(tenantId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to handle OAuth callback and exchange code for tokens.
     *
     * @param tenantId the tenant identifier from header
     * @param request FinchAuthCodeRequest containing authorization code
     * @return ResponseEntity with JsonNode containing token response
     * @throws Exception if token exchange fails
     */
    @PostMapping("/callback")
    @Operation(summary = "Handle OAuth callback", description = "Processes OAuth callback and exchanges authorization code for access and refresh tokens")
    public ResponseEntity<JsonNode> handleCallback(@CurrentUser User user, @RequestBody FinchAuthCodeRequest request) throws Exception {
        String tenantId = user.getTenantId();
        JsonNode tokenResponse = finchService.exchangeCodeForTokens(request.getCode());
        tokenManager.storeToken(tenantId, tokenResponse);
        // Clear reauthentication flag since new tokens are received
        tokenManager.clearReauthRequired(tenantId);
        // Trigger async employee sync, then payroll processing on success
                log.info("Starting async employee sync for tenantId={}", tenantId);
                batchEmployeeService.executeEmployeeSyncJob(tenantId);
                onboardingService.updateState(tenantId, OnBoardingState.FINCH);
                log.info("Async employee sync completed for tenantId={}", tenantId);
                // Trigger payroll processing after successful employee sync
                    LocalDate now = LocalDate.now();
                    LocalDate start = now.withDayOfMonth(1);
                    LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
                    prePayrollBatchService.processPayrollBatch(tenantId, start, end);
                    log.info("Payroll batch processing triggered for tenantId={}, period: {} to {}", tenantId, start, end);
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Endpoint to retrieve valid access token for tenant.
     *
     * @param user Current authenticated user
     * @return ResponseEntity with access token string or reauthentication error
     * @throws Exception if token retrieval fails
     */
    @GetMapping("/token")
    @Operation(summary = "Get valid access token", description = "Retrieves valid access token for tenant, refreshing if expired")
    public ResponseEntity<?> getAccessToken(@CurrentUser User user) throws Exception {
        String tenantId = user.getTenantId();
        // Get token with automatic reauthentication handling
        TokenAccessResult result = tokenManager.getTokenWithReauthHandling(tenantId);
        if (result.isError()) {
            return ResponseEntity.status(400).body(result.getErrorResponse());
        }
        return ResponseEntity.ok(result.getToken());
    }
} 