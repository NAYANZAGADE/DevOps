package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "plan_signature")
@EqualsAndHashCode(callSuper = true)
public class PlanSignatureEntity extends BaseEntity {
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "signature_text", nullable = false, columnDefinition = "TEXT")
    private String signatureText;
    
    @Column(name = "signature_timestamp", nullable = false)
    private LocalDateTime signatureTimestamp;
    
    @Column(name = "is_documents_read", nullable = false)
    private Boolean isDocumentsRead;
    
    @Column(name = "is_changes_understood", nullable = false)
    private Boolean isChangesUnderstood;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmation_id")
    private TrusteeConfirmationEntity confirmation;
} 