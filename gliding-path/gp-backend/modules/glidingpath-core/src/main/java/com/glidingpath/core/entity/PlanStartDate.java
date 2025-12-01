package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

/**
 * Entity for storing plan start and key event dates.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class PlanStartDate extends BaseEntity {

    private LocalDate startDate;
    private LocalDate onboardingTasksDue;
    private LocalDate employeeInvitesSent;
    private LocalDate paycheckWithFirstContribution;

    @ManyToOne
    @JoinColumn(name = "tenant_plan_id")
    private TenantPlan tenantPlan;
}
