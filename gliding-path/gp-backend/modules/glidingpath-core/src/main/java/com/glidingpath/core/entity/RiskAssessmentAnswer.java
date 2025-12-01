package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity for storing user responses to risk assessment questionnaire
 * Extends BaseEntity to inherit common fields (id, tenantId, timestamps, etc.)
 */
@Entity
@Table(name = "risk_assessment_answers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RiskAssessmentAnswer extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

  
    public RiskAssessmentAnswer(String tenantId, String userId, Integer questionId, String answer) {
        super();
        this.setTenantId(tenantId);
        this.userId = userId;
        this.questionId = questionId;
        this.answer = answer;
    }
}
