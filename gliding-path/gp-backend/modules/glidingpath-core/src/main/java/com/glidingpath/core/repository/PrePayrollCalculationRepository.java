package com.glidingpath.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.glidingpath.core.entity.PrePayrollCalculation;
import com.glidingpath.core.entity.PrePayrollCalculation.CalculationStatus;

@Repository
public interface PrePayrollCalculationRepository extends JpaRepository<PrePayrollCalculation, UUID> {
    
    /**
     * Find calculations by tenant ID
     */
    List<PrePayrollCalculation> findByTenantId(String tenantId);
    
    /**
     * Find calculations by payroll period
     */
    @Query("SELECT p FROM PrePayrollCalculation p WHERE p.tenantId = :tenantId " +
           "AND p.payrollPeriodStart = :startDate AND p.payrollPeriodEnd = :endDate")
    List<PrePayrollCalculation> findByPayrollPeriod(
        @Param("tenantId") String tenantId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Find calculation by unique calculation ID
     */
    Optional<PrePayrollCalculation> findByCalculationId(String calculationId);
    
    /**
     * Find calculations by multiple calculation IDs
     */
    List<PrePayrollCalculation> findByCalculationIdIn(List<String> calculationIds);
    
    /**
     * Find calculations that need reprocessing (failed or pending)
     */
    @Query("SELECT p FROM PrePayrollCalculation p WHERE p.status IN ('FAILED', 'PENDING') " +
           "AND p.tenantId = :tenantId")
    List<PrePayrollCalculation> findCalculationsForReprocessing(@Param("tenantId") String tenantId);
    
    /**
     * Find latest calculation for employee by tenant and employee individual ID
     * Using the employee relationship instead of direct employeeId field
     */
    @Query("SELECT p FROM PrePayrollCalculation p WHERE p.tenantId = :tenantId " +
           "AND p.employee.individualId = :employeeId ORDER BY p.updatedAt DESC")
    List<PrePayrollCalculation> findByTenantIdAndEmployee_IndividualIdOrderByUpdatedAtDesc(
        @Param("tenantId") String tenantId, 
        @Param("employeeId") String employeeId
    );
    
    /**
     * Find calculations by tenant ID and calculation status with employee eagerly loaded
     */
    @Query("SELECT p FROM PrePayrollCalculation p JOIN FETCH p.employee WHERE p.tenantId = :tenantId AND p.status = :status")
    List<PrePayrollCalculation> findByTenantIdAndStatus(
        @Param("tenantId") String tenantId, 
        @Param("status") CalculationStatus status
    );
    
    /**
     * Find calculations by tenant ID and calculation status with employee relationship eagerly loaded
     */
    @Query("SELECT p FROM PrePayrollCalculation p JOIN FETCH p.employee e WHERE p.tenantId = :tenantId AND p.status = :status")
    List<PrePayrollCalculation> findByTenantIdAndStatusWithEmployee(
        @Param("tenantId") String tenantId, 
        @Param("status") CalculationStatus status
    );
} 