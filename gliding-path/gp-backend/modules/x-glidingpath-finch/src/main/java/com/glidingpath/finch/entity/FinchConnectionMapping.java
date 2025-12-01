package com.glidingpath.finch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "finch_connection_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinchConnectionMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String connectionId;

    @Column(nullable = false)
    private String tenantId;
} 