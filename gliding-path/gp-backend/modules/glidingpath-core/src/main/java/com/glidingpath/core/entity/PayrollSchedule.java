package com.glidingpath.core.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollSchedule {
    private String schedule;
    private int numberOfDays;
}