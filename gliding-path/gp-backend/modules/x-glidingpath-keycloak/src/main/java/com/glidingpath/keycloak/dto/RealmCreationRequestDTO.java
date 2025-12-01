package com.glidingpath.keycloak.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealmCreationRequestDTO {
    
    @NotBlank(message = "Organization name is required")
    private String organizationName;
    
    @NotBlank(message = "Organization slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Organization slug must contain only lowercase letters, numbers, and hyphens")
    private String organizationSlug;
    
    @NotBlank(message = "Organization email domain is required")
    private String emailDomain;
    
    private String description;
    
    private String adminEmail;
    
    private String adminFirstName;
    
    private String adminLastName;
} 