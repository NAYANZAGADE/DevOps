package com.glidingpath.keycloak.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealmCreationResponseDTO {
    
    private String realmName;
    private String organizationName;
    private String status; // SUCCESS, FAILED, ALREADY_EXISTS
    private String message;
    private LocalDateTime createdAt;
    private List<String> createdClients;
    private List<String> createdRoles;
    private String adminUserId;
    private String adminUsername;
} 