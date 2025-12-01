package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trustee_confirmation")
@EqualsAndHashCode(callSuper = true)
public class TrusteeConfirmationEntity extends BaseEntity {
    
    @Column(name = "is_trustee", nullable = false)
    private Boolean isTrustee;
    
    @Column(name = "is_agree", nullable = false)
    private Boolean isAgree;
    
    @Column(name = "is_authorize", nullable = false)
    private Boolean isAuthorize;
    
    @Column(name = "confirmation_timestamp", nullable = false)
    private LocalDateTime confirmationTimestamp;
    
    @Column(name = "trustee_title")
    private String trusteeTitle;
    
    @Column(name = "trustee_legal_name")
    private String trusteeLegalName;
    
    @Column(name = "trustee_email")
    private String trusteeEmail;
} 