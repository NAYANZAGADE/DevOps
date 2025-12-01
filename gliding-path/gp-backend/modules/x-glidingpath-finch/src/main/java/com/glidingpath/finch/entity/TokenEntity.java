package com.glidingpath.finch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenEntity {
    
	@Id
    private String tenantId;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
    
    @Column(name = "connection_id")
    private String connectionId;
    
    @Column(name = "customer_id")
    private String customerId;
    
    @Column(name = "last_reauth_at")
    private Instant lastReauthAt;
    
    @Column(name = "reauth_required")
    @Builder.Default
    private Boolean reauthRequired = false;
}
