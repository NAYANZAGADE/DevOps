package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.sql.Timestamp;

@Entity
@Table(name = "documents")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documents extends BaseEntity {
    @Column(nullable = false)
    private String status;

    @Column(name = "file_key")
    private String fileKey;
} 