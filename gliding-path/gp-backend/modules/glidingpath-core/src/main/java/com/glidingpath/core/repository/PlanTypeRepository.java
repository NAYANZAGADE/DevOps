package com.glidingpath.core.repository;

import com.glidingpath.core.entity.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanTypeRepository extends JpaRepository<PlanType, UUID> {}
