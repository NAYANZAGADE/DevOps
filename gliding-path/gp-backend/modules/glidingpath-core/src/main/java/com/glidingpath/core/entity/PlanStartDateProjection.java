package com.glidingpath.core.entity;
import java.time.LocalDate;

/**
 * Projection interface to map selected columns for plan dates.
 */
public interface PlanStartDateProjection {
    LocalDate getStartDate();
    LocalDate getOnboardingTasksDue();
    LocalDate getEmployeeInvitesSent();
    LocalDate getPaycheckWithFirstContribution();
}

