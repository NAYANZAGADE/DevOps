package com.glidingpath.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glidingpath.core.entity.CompanyDetailsEntity;

import java.util.Optional;
import java.util.UUID;

public interface CompanyDetailsRepository extends JpaRepository<CompanyDetailsEntity, UUID> {
    Optional<CompanyDetailsEntity> findByTenantId(String tenantId);
}