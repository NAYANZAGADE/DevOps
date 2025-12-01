package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "tenantId", column = @Column(name = "tenant_id", insertable = false, updatable = false))
public class User extends BaseEntity {
    
    private String preferredUsername;
    private String email;
    private String sub; // Keycloak's unique user id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
} 