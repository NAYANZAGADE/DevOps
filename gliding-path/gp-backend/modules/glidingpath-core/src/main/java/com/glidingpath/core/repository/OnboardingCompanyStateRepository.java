package com.glidingpath.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glidingpath.core.entity.OnboardingCompanyState;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OnboardingCompanyStateRepository extends JpaRepository<OnboardingCompanyState, UUID> {
    Optional<OnboardingCompanyState> findByTenantId(String tenantId);
}