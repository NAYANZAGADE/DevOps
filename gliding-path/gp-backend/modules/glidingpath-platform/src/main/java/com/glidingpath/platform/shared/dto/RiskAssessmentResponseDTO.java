package com.glidingpath.platform.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for returning risk assessment questions and answers
 * Contains complete question information with user's answer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResponseDTO {

    private List<RiskQuestionDTO> questions;
}
