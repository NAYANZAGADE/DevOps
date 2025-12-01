package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "finch_benefits")
public class Benefit extends BaseEntity {
    
    @Column(name = "benefit_id", unique = true, nullable = false)
    private String benefitId;
    
    @Column(name = "benefit_type")
    private String type;
    private String description;
    private String frequency;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "type", column = @Column(name = "company_contribution_type"))
    })
    private CompanyContribution companyContribution;
    
    @Embeddable
    @Data
    public static class CompanyContribution {
        private String type; // e.g., "match"
        
        @ElementCollection
        @CollectionTable(name = "finch_benefit_contribution_tiers", joinColumns = @JoinColumn(name = "benefit_id"))
        private List<ContributionTier> tiers;
    }
    
    @Embeddable
    @Data
    public static class ContributionTier {
        private Integer threshold;
        private Integer match;
    }
} 