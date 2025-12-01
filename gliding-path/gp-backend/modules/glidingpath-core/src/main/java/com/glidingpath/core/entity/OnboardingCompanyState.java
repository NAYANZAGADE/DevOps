package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import com.glidingpath.common.util.OnBoardingState;

import java.time.LocalDateTime;

/**
 * Entity representing company onboarding state
 * Tracks the current step of a company's onboarding process
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_onboarding_state")
public class OnboardingCompanyState extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false)
    private OnBoardingState currentState;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}