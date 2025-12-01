package com.glidingpath.core.repository;

import com.glidingpath.core.entity.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for system configuration table.
 */
@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, java.util.UUID> {
    
    /**
     * Find configuration by key
     */
    Optional<SystemConfiguration> findByConfigKey(String configKey);
    
    /**
     * Find all active configurations by type
     */
    List<SystemConfiguration> findByConfigTypeAndIsActiveTrue(String configType);
    
    /**
     * Find all active configurations
     */
    List<SystemConfiguration> findByIsActiveTrue();
    
    /**
     * Find configuration by key and ensure it's active
     */
    Optional<SystemConfiguration> findByConfigKeyAndIsActiveTrue(String configKey);
} 