package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "plan_type_feature")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanTypeFeature extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_type_id", nullable = false)
	private PlanType planType;

	@Column(name = "label", nullable = false)
	private String label;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;
} 