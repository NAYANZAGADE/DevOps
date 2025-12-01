package com.glidingpath.platform.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrusteeConfirmationRequestDTO {
    @NotNull(message = "isTrustee is required")
    private Boolean isTrustee;
    
    @NotNull(message = "isAgree is required")
    private Boolean isAgree;
    
    @NotNull(message = "isAuthorize is required")
    private Boolean isAuthorize;
    
    private String trusteeTitle;
    
    private String trusteeLegalName;
    
    private String trusteeEmail;
} 