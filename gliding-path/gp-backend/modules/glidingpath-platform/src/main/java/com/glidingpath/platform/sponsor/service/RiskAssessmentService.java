package com.glidingpath.platform.sponsor.service;

import com.glidingpath.platform.shared.dto.RiskAssessmentRequestDTO;
import com.glidingpath.platform.shared.dto.RiskAssessmentResponseDTO;
import com.glidingpath.platform.shared.dto.RiskQuestionDTO;

import java.util.List;

/**
 * Service interface for Risk Assessment functionality
 * Handles questionnaire operations and user answer management
 */
public interface RiskAssessmentService {

    /**
     * Save user answers for risk assessment questionnaire
     * @param tenantId Tenant identifier
     * @param request Request containing user ID and answers
     * @return Response with questions and user's answers
     */
    RiskAssessmentResponseDTO saveAnswers(String tenantId, RiskAssessmentRequestDTO request);

    /**
     * Get user's saved answers for risk assessment questionnaire
     * @param tenantId Tenant identifier
     * @param userId User identifier
     * @return Response with questions and user's saved answers
     */
    RiskAssessmentResponseDTO getUserAnswers(String tenantId, String userId);

}
