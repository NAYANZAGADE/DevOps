package com.glidingpath.platform.sponsor.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import com.glidingpath.common.util.OnBoardingState;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnBoardingCompanyStateResponseDTO {
    private String tenantId;
    private OnBoardingState currentState;
    private LocalDateTime updatedAt; 
    private LocalDateTime createdAt;
} 