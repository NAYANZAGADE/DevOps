package com.glidingpath.core.repository;

import com.glidingpath.core.entity.TrusteeConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrusteeConfirmationRepository extends JpaRepository<TrusteeConfirmationEntity, UUID> {
    
    TrusteeConfirmationEntity findByTenantId(String tenantId);
} 