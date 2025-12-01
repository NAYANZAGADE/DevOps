package com.glidingpath.platform.sponsor.service.impl;



import com.glidingpath.common.util.OnBoardingState;
import com.glidingpath.core.entity.OnboardingCompanyState;
import com.glidingpath.core.repository.OnboardingCompanyStateRepository;
import com.glidingpath.platform.sponsor.dto.OnBoardingCompanyStateResponseDTO;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingCompanyStateServiceImpl implements OnboardingCompanyStateService {

    private final OnboardingCompanyStateRepository repository;

    @Override
    @Transactional(readOnly = true)
    public OnBoardingCompanyStateResponseDTO getStateByTenant(String tenantId) {
        OnboardingCompanyState state = repository.findByTenantId(tenantId)
                .orElseGet(() -> new OnboardingCompanyState());
        return new OnBoardingCompanyStateResponseDTO(
                state.getTenantId(),
                state.getCurrentState(),
                state.getUpdatedAt(),
                state.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public OnBoardingCompanyStateResponseDTO updateState(String tenantId, OnBoardingState newState) {
        OnboardingCompanyState state = repository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    OnboardingCompanyState newStateEntity = new OnboardingCompanyState();
                    newStateEntity.setTenantId(tenantId);
                    newStateEntity.setCurrentState(newState);
                    return newStateEntity;
                });
        if (state.getId() != null) {
            state.setCurrentState(newState);
        }
        OnboardingCompanyState savedState = repository.save(state);
        return new OnBoardingCompanyStateResponseDTO(
                savedState.getTenantId(),
                savedState.getCurrentState(),
                savedState.getUpdatedAt(),
                savedState.getCreatedAt()
        );
    }
}