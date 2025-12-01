package com.glidingpath.platform.sponsor.service;


import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.platform.sponsor.dto.OnBoardingCompanyStateResponseDTO;


public interface OnboardingCompanyStateService {
    OnBoardingCompanyStateResponseDTO getStateByTenant(String tenantId);
    OnBoardingCompanyStateResponseDTO updateState(String tenantId, OnBoardingState newState);
} 