package com.glidingpath.platform.shared.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PlanSignatureResponseDTO {
    
    private UUID id;
    private String tenantId;
    private String firstName;
    private String lastName;
    private String signatureText;
    private LocalDateTime signatureTimestamp;
    private Boolean isDocumentsRead;
    private Boolean isChangesUnderstood;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 