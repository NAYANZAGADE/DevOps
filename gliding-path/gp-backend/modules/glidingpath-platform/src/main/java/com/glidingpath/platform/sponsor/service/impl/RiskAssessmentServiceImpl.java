package com.glidingpath.platform.sponsor.service.impl;

import com.glidingpath.core.entity.RiskAssessmentAnswer;
import com.glidingpath.core.repository.RiskAssessmentAnswerRepository;
import com.glidingpath.platform.shared.dto.RiskAssessmentRequestDTO;
import com.glidingpath.platform.shared.dto.RiskAssessmentResponseDTO;
import com.glidingpath.platform.shared.dto.RiskQuestionDTO;
import com.glidingpath.platform.sponsor.service.RiskAssessmentService;
import constants.RiskAssessmentConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for Risk Assessment functionality
 * Handles questionnaire operations and user answer management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final RiskAssessmentAnswerRepository repository;

    @Override
    @Transactional
    public RiskAssessmentResponseDTO saveAnswers(String tenantId, RiskAssessmentRequestDTO request) {
        log.info("Saving risk assessment answers for tenant: {}, user: {}", tenantId, request.getUserId());
        
        String userId = request.getUserId();
        Map<Integer, String> answers = request.getAnswers();       
        // Save or update each answer
        for (Map.Entry<Integer, String> entry : answers.entrySet()) {
            Integer questionId = entry.getKey();
            String answer = entry.getValue();
            
            Optional<RiskAssessmentAnswer> existingAnswer = 
                repository.findByTenantIdAndUserIdAndQuestionId(tenantId, userId, questionId);
            
            if (existingAnswer.isPresent()) {
                // Update existing answer
                RiskAssessmentAnswer answerEntity = existingAnswer.get();
                answerEntity.setAnswer(answer);
                repository.save(answerEntity);
                log.debug("Updated answer for question {}: {}", questionId, answer);
            } else {
                // Create new answer
                RiskAssessmentAnswer answerEntity = new RiskAssessmentAnswer(tenantId, userId, questionId, answer);
                repository.save(answerEntity);
                log.debug("Created new answer for question {}: {}", questionId, answer);
            }
        }
        
        // Return updated response with all questions and answers
        return getUserAnswers(tenantId, userId);
    }

    @Override
    public RiskAssessmentResponseDTO getUserAnswers(String tenantId, String userId) {
        log.info("Retrieving risk assessment answers for tenant: {}, user: {}", tenantId, userId);
        
        // Get user's saved answers
        List<RiskAssessmentAnswer> userAnswers = repository.findByTenantIdAndUserId(tenantId, userId);
        Map<Integer, String> answerMap = userAnswers.stream()
            .collect(Collectors.toMap(
                RiskAssessmentAnswer::getQuestionId,
                RiskAssessmentAnswer::getAnswer
            ));
        
        // Build response with all questions and user's answers
        List<RiskQuestionDTO> questions = new ArrayList<>();
        
        for (Integer questionId : RiskAssessmentConstants.getAllQuestionIds()) {
            String questionText = RiskAssessmentConstants.getQuestionText(questionId);
            String userAnswer = answerMap.get(questionId);
            
            questions.add(new RiskQuestionDTO(questionId, questionText, userAnswer));
        }
        
        log.info("Retrieved {} questions with {} answered for user: {}", 
                questions.size(), answerMap.size(), userId);
        
        return new RiskAssessmentResponseDTO(questions);
    }

}
