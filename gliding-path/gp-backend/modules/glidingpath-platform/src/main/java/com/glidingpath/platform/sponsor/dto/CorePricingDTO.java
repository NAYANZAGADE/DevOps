package com.glidingpath.platform.sponsor.dto;

public record CorePricingDTO(
        String baseFee,
        String participantFee,
        String employerAccountFee,
        String employeeAccountFee
) {} 