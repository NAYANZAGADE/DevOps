package com.glidingpath.core.repository;

import com.glidingpath.core.entity.PlanSponsor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanSponsorRepository extends JpaRepository<PlanSponsor, UUID> {
    Optional<PlanSponsor> findByTenantId(String tenantId);
} 