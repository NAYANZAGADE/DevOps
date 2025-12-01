package com.glidingpath.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinchBenefitDTO {
    private String benefit_id;
    private CompanyContribution company_contribution;
    private String type;
    private String description;
    private String frequency;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompanyContribution {
        private String type; // e.g., "match"
        private List<ContributionTier> tiers;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContributionTier {
        private Integer threshold;
        private Integer match;
    }
} 