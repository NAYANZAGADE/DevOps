package com.glidingpath.core.repository;

import com.glidingpath.core.entity.VestingScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VestingScheduleDetailRepository extends JpaRepository<VestingScheduleDetail, UUID> {
    
    List<VestingScheduleDetail> findByVestingScheduleId(UUID vestingScheduleId);
    
    List<VestingScheduleDetail> findByTenantVestingScheduleId(UUID tenantVestingScheduleId);
} 