package com.glidingpath.rules.repository;

import com.glidingpath.rules.entity.RuleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RuleConfigRepository extends JpaRepository<RuleConfig, Long> {
    Optional<RuleConfig> findByName(String name);
} 