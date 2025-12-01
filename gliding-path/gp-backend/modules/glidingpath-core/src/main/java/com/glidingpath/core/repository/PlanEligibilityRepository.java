package com.glidingpath.core.repository;

import com.glidingpath.core.entity.PlanEligibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanEligibilityRepository extends JpaRepository<PlanEligibility, UUID> {
    
    /**
     * Find eligibility rules by tenant ID
     */
    Optional<PlanEligibility> findByTenantId(String tenantId);
} 