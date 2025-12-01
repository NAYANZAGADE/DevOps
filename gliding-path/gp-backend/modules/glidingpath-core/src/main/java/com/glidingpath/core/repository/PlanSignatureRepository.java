package com.glidingpath.core.repository;

import com.glidingpath.core.entity.PlanSignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanSignatureRepository extends JpaRepository<PlanSignatureEntity, UUID> {
    
    PlanSignatureEntity findByTenantId(String tenantId);
} 