package com.glidingpath.platform.sponsor.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDetailsDTO {
    private String legalName;
    private String ein;
    private String email;
    private BusinessAddressDTO businessAddress;
    private String entityType;
    private String payrollProvider;
    private PayrollScheduleDTO payrollSchedule;
    private int estimatedEmployeeCount;
    private boolean unionEmployees;
    private boolean leasedEmployees;
    private boolean existingRetirementPlan;
    private boolean relatedEntities;
    private String employmentStatus; 
    private String businessSize;
    private String retirementPlanPriority; 
    private boolean hasExisting401k;
    private boolean hasMultipleBusinesses;

    @Data
    public static class BusinessAddressDTO {
        // Business Address Fields
        private String street;
        private String apt;
        private String city;
        private String state;
        private String postalCode;
        private String phoneNumber;
        private boolean mailingDifferent;
        
        // Mailing Address Fields (when mailingDifferent = true)
        private String mailingStreet;
        private String mailingApt;
        private String mailingCity;
        private String mailingState;
        private String mailingPostalCode;
        private String mailingPhoneNumber;
    }

    @Data
    public static class PayrollScheduleDTO {
        private String schedule;
        private int numberOfDays;
    }
} 