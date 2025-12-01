package com.glidingpath.core.repository;

import com.glidingpath.core.entity.RiskAssessmentAnswer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RiskAssessmentAnswer entity
 * Extends BaseRepository to inherit tenant-based queries
 */
@Repository
public interface RiskAssessmentAnswerRepository extends BaseRepository<RiskAssessmentAnswer> {

    /**
     * Find all answers for a specific user within a tenant
     */
    List<RiskAssessmentAnswer> findByTenantIdAndUserId(String tenantId, String userId);

    /**
     * Find a specific answer for a user and question within a tenant
     */
    Optional<RiskAssessmentAnswer> findByTenantIdAndUserIdAndQuestionId(
        String tenantId, String userId, Integer questionId);

    /**
     * Check if an answer exists for a specific user and question within a tenant
     */
    boolean existsByTenantIdAndUserIdAndQuestionId(
        String tenantId, String userId, Integer questionId);

    /**
     * Delete all answers for a specific user within a tenant
     */
    void deleteByTenantIdAndUserId(String tenantId, String userId);

    /**
     * Delete a specific answer for a user and question within a tenant
     */
    void deleteByTenantIdAndUserIdAndQuestionId(
        String tenantId, String userId, Integer questionId);

    /**
     * Find answers by question ID across all tenants (for analytics)
     */
    @Query("SELECT r FROM RiskAssessmentAnswer r WHERE r.questionId = :questionId")
    List<RiskAssessmentAnswer> findByQuestionId(@Param("questionId") Integer questionId);

    /**
     * Count total answers for a specific user within a tenant
     */
    long countByTenantIdAndUserId(String tenantId, String userId);
}
