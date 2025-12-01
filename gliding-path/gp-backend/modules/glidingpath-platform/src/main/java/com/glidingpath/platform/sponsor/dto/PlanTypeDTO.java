package com.glidingpath.platform.sponsor.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import java.util.List;
@Data
public class PlanTypeDTO {
    private String name;
    private String description;
    private BigDecimal monthlyCost;
    private BigDecimal perParticipantFee;
    private BigDecimal employerAccountFee;
    private BigDecimal employeeAccountFee;
    private String employerContribution;
    private Integer employeeContributionLimit;
    private String complianceProtection;
    private String taxCredit;
    private UUID id;
    private List<String> features;
    private String headline;
    private String longDescription;
} 