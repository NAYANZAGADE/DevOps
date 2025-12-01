package com.glidingpath.core.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "tenantId", column = @Column(name = "tenant_id", insertable = false, updatable = false))
public class Tenant extends BaseEntity {
    private String orgId;
    private String displayName;
} 