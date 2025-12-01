package com.glidingpath.core.repository;

import com.glidingpath.core.entity.MasterVestingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MasterVestingScheduleRepository extends JpaRepository<MasterVestingSchedule, UUID> {
} 