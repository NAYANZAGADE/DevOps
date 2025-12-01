package com.glidingpath.core.entity;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entity for system configuration table that stores JSON configuration objects.
 * This is a master table - only admins can modify these configurations.
 */
@Entity
@Table(name = "sys_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemConfiguration extends BaseEntity {
    
    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey;
    
    @Column(name = "config_value", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode configValue;
    
    @Column(name = "config_type", nullable = false)
    private String configType;
    
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
} 