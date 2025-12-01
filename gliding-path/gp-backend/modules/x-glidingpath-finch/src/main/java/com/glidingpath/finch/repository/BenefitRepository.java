package com.glidingpath.finch.repository;

import com.glidingpath.core.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, UUID> {
    
    Optional<Benefit> findByBenefitId(String benefitId);
    
    Optional<Benefit> findByBenefitIdAndTenantId(String benefitId, String tenantId);
    
    List<Benefit> findByTenantId(String tenantId);
    
    @Query("SELECT b FROM Benefit b WHERE b.tenantId = :tenantId ORDER BY b.createdAt DESC")
    List<Benefit> findByTenantIdOrderByCreatedAtDesc(@Param("tenantId") String tenantId);
    
    boolean existsByBenefitIdAndTenantId(String benefitId, String tenantId);
} 