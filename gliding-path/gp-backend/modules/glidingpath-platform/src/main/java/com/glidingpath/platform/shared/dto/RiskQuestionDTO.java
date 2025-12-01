package com.glidingpath.platform.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for individual risk assessment question
 * Contains question text and user's answer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskQuestionDTO {

    private Integer questionId;
    private String question;
    private String answer;

    /**
     * Constructor for questions without answer
     */
    public RiskQuestionDTO(Integer questionId, String question) {
        this.questionId = questionId;
        this.question = question;
        this.answer = null;
    }
}
