package com.glidingpath.finch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glidingpath.finch.entity.FinchConnectionMapping;

import java.util.Optional;

@Repository
public interface FinchConnectionMappingRepository extends JpaRepository<FinchConnectionMapping, Long> {
    Optional<FinchConnectionMapping> findByConnectionId(String connectionId);
} 