package com.glidingpath.platform.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * DTO for submitting risk assessment answers
 * Used by frontend to send user responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentRequestDTO {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotEmpty(message = "Answers cannot be empty")
    private Map<Integer, String> answers;
}
