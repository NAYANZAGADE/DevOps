package com.glidingpath.core.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.glidingpath.core.enums.ExclusionType;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PlanEligibility extends BaseEntity {
	private Integer minimumEntryAge;
	private Integer timeEmployedMonths;

	@ElementCollection(targetClass = ExclusionType.class)
	@CollectionTable(name = "plan_eligibility_exclusion", joinColumns = @JoinColumn(name = "plan_eligibility_id"))
	@Enumerated(EnumType.STRING)
	private Set<ExclusionType> exclusions;
}
