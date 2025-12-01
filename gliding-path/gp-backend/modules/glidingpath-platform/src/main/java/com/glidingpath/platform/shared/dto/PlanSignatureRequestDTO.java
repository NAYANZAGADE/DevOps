package com.glidingpath.platform.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlanSignatureRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Signature text is required")
    private String signatureText;
    
    @NotNull(message = "Documents read confirmation is required")
    private Boolean isDocumentsRead;
    
    @NotNull(message = "Changes understood confirmation is required")
    private Boolean isChangesUnderstood;
} 