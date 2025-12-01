package com.glidingpath.platform.shared.controllers;

import com.glidingpath.platform.shared.dto.RiskAssessmentRequestDTO;
import com.glidingpath.platform.shared.dto.RiskAssessmentResponseDTO;
import com.glidingpath.platform.shared.dto.RiskQuestionDTO;
import com.glidingpath.platform.sponsor.service.RiskAssessmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Risk Assessment Questionnaire APIs
 * Handles retirement planning risk assessment questionnaire operations
 */
@Slf4j
@RestController
@RequestMapping("/api/risk-assessment")
@RequiredArgsConstructor
@Tag(name = "Risk Assessment", description = "APIs for retirement planning risk assessment questionnaire")
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;


    /**
     * Submit user answers for risk assessment questionnaire
     * @param tenantId Tenant identifier (from request header or path)
     * @param request Request containing user ID and answers
     * @return Response with questions and user's answers
     */
    @PostMapping("/answers")
    @Operation(summary = "Submit risk assessment answers", 
               description = "Saves user answers for the risk assessment questionnaire")
    public ResponseEntity<RiskAssessmentResponseDTO> submitAnswers(
            @CurrentUser User user,
            @Valid @RequestBody RiskAssessmentRequestDTO request) {
                String tenantId = user.getTenantId();
        
        log.info("POST /api/risk-assessment/answers - Submitting answers for tenant: {}, user: {}", 
                tenantId, request.getUserId());
        
        RiskAssessmentResponseDTO response = riskAssessmentService.saveAnswers(tenantId, request);
        
        log.info("Successfully saved {} answers for user: {}", 
                request.getAnswers().size(), request.getUserId());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's saved answers for risk assessment questionnaire
     * @param tenantId Tenant identifier (from request header or path)
     * @param userId User identifier
     * @return Response with questions and user's saved answers
     */
    @GetMapping("/answers/{userId}")
    @Operation(summary = "Get user's risk assessment answers", 
               description = "Retrieves user's saved answers for the risk assessment questionnaire")
    public ResponseEntity<RiskAssessmentResponseDTO> getUserAnswers(
            @CurrentUser User user,
            @Parameter(description = "User identifier") @PathVariable String userId) {
                String tenantId = user.getTenantId();
        
        log.info("GET /api/risk-assessment/answers/{} - Retrieving answers for tenant: {}", userId, tenantId);
        
        RiskAssessmentResponseDTO response = riskAssessmentService.getUserAnswers(tenantId, userId);
        
        long answeredCount = response.getQuestions().stream()
            .mapToLong(q -> q.getAnswer() != null && !q.getAnswer().trim().isEmpty() ? 1 : 0)
            .sum();
        
        log.info("Successfully retrieved {} answered questions out of {} total for user: {}", 
                answeredCount, response.getQuestions().size(), userId);
        
        return ResponseEntity.ok(response);
    }

  

}
