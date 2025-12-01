package com.glidingpath.core.repository;


import com.glidingpath.core.entity.PlanStartDate;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for PlanStartDate entity.
 */
public interface PlanStartDateRepository extends JpaRepository<PlanStartDate, UUID> {
}
