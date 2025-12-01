package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "plan_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanType  extends BaseEntity{

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
    
    @Column(name = "headline")
    private String headline;

    @Column(name = "long_description")
    private String longDescription;

    @OneToMany(mappedBy = "planType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("displayOrder ASC")
    private List<PlanTypeFeature> features = new ArrayList<>();
}