package com.glidingpath.platform.sponsor.dto;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.FutureOrPresent;
import java.util.UUID;

@Data
public class PlanStartDateDTO {
    @FutureOrPresent(message = "Start date must be today or a future date.")
    private LocalDate startDate;

    @FutureOrPresent(message = "Onboarding tasks due date must be today or a future date.")
    private LocalDate onboardingTasksDue;

    @FutureOrPresent(message = "Employee invites sent date must be today or a future date.")
    private LocalDate employeeInvitesSent;

    @FutureOrPresent(message = "Paycheck with first contribution date must be today or a future date.")
    private LocalDate paycheckWithFirstContribution;

    private UUID tenantPlanId;
} 