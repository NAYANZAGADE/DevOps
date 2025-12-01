package com.glidingpath.core.repository;

import com.glidingpath.core.entity.Documents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentStatusRepository extends JpaRepository<Documents, java.util.UUID> {
    
    List<Documents> findAllByTenantId(String tenantId);
    
    Optional<Documents> findByFileKey(String fileKey);
    
    @Modifying
    @Query("DELETE FROM Documents d WHERE d.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") String tenantId);
} 