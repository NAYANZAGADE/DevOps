package com.glidingpath.core.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plan_sponsor_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDetailsEntity extends BaseEntity {
    @Column(name = "legal_name")
    private String legalName;
    
    private String ein;
    
    @Column(name = "email")
    private String email;

    @Embedded
    private BusinessAddress businessAddress;

    @Column(name = "entity_type")
    private String entityType;
    
    @Column(name = "payroll_provider")
    private String payrollProvider;

    @Embedded
    private PayrollSchedule payrollSchedule;

    @Column(name = "estimated_employee_count")
    private int estimatedEmployeeCount;
    
    @Column(name = "union_employees")
    private boolean unionEmployees;
    
    @Column(name = "leased_employees")
    private boolean leasedEmployees;
    
    @Column(name = "existing_retirement_plan")
    private boolean existingRetirementPlan;
    
    @Column(name = "related_entities")
    private boolean relatedEntities;
    
    @Column(name = "employment_status")
    private String employmentStatus; 
    
    @Column(name = "business_size")
    private String businessSize; 
    
    @Column(name = "retirement_plan_priority")
    private String retirementPlanPriority; 
    
    @Column(name = "has_existing_401k")
    private boolean hasExisting401k;
    
    @Column(name = "has_multiple_businesses")
    private boolean hasMultipleBusinesses;

    @Embeddable
    @Data
    public static class BusinessAddress {
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

    @Embeddable
    @Data
    public static class PayrollSchedule {
        private String schedule;
        private int numberOfDays;
    }
}