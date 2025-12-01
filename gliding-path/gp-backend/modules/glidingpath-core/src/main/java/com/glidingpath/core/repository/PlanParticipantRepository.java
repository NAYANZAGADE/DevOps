package com.glidingpath.core.repository;

import com.glidingpath.core.entity.PlanParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PlanParticipantRepository extends JpaRepository<PlanParticipant, UUID> {
    Optional<PlanParticipant> findByIndividualId(String individualId);
    
    Optional<PlanParticipant> findByIndividualIdAndTenantId(String individualId, String tenantId);
    
    List<PlanParticipant> findByIndividualIdIn(Set<String> individualIds);
    
    @Query("SELECT f FROM PlanParticipant f WHERE f.tenantId = :tenantId ORDER BY f.createdAt DESC")
    List<PlanParticipant> findByTenantIdOrderByCreatedAtDesc(@Param("tenantId") String tenantId);
    
    // Safe query that doesn't trigger lazy loading of collections
    @Query("SELECT p FROM PlanParticipant p WHERE p.tenantId = :tenantId ORDER BY p.createdAt DESC")
    List<PlanParticipant> findByTenantIdOrderByCreatedAtDescSafe(@Param("tenantId") String tenantId);
    
    @Query("SELECT f FROM PlanParticipant f WHERE f.tenantId = :tenantId ORDER BY f.createdAt DESC LIMIT :limit")
    List<PlanParticipant> findRecentByTenantIdOrderByCreatedAtDesc(@Param("tenantId") String tenantId, @Param("limit") int limit);
    
    @Modifying
    @Query("DELETE FROM PlanParticipant f WHERE f.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") String tenantId);
    
    // New methods for improved batch processing
    @Query("SELECT COUNT(f) FROM PlanParticipant f WHERE f.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
    
    @Query("SELECT f FROM PlanParticipant f WHERE f.tenantId = :tenantId ORDER BY f.createdAt DESC LIMIT :limit")
    List<PlanParticipant> findByTenantIdOrderByCreatedAtDescWithLimit(@Param("tenantId") String tenantId, @Param("limit") int limit);
    
    // Pagination method for batch processing
    Page<PlanParticipant> findByTenantId(String tenantId, Pageable pageable);
    
    /**
     * Find participants by tenant ID with name search functionality
     * 
     * @param tenantId the tenant identifier
     * @param search search term for first name or last name
     * @param pageable pagination information
     * @return Page of PlanParticipant entities matching the criteria
     */
    @Query("SELECT p FROM PlanParticipant p WHERE p.tenantId = :tenantId " +
           "AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PlanParticipant> findByTenantIdAndNameContaining(@Param("tenantId") String tenantId, 
                                                          @Param("search") String search, 
                                                          Pageable pageable);
} 