package com.glidingpath.platform.shared.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TrusteeConfirmationResponseDTO {
    
    private UUID id;
    private String tenantId;
    private Boolean isTrustee;
    private Boolean isAgree;
    private Boolean isAuthorize;
    private LocalDateTime confirmationTimestamp;
    private String trusteeTitle;
    private String trusteeLegalName;
    private String trusteeEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 