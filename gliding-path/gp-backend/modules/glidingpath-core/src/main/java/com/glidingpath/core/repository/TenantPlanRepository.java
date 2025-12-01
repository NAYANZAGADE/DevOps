package com.glidingpath.core.repository;

import com.glidingpath.core.entity.TenantPlan;
import com.glidingpath.core.entity.CorePricingProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TenantPlanRepository extends JpaRepository<TenantPlan, UUID> {
    List<TenantPlan> findByTenantId(String tenantId);
    
    @Query(value = "SELECT " +
           "pt.monthly_cost as baseFee, " +
           "pt.per_participant_fee as participantFee, " +
           "pt.employer_account_fee as employerAccountFee, " +
           "pt.employee_account_fee as employeeAccountFee " +
           "FROM tenant_plan tp " +
           "JOIN plan_type pt ON tp.plan_type_id = pt.id " +
           "WHERE tp.tenant_id = :tenantId " +
           "ORDER BY tp.created_at DESC " +
           "LIMIT 1", nativeQuery = true)
    CorePricingProjection findCorePricingByTenantId(@Param("tenantId") String tenantId);
}