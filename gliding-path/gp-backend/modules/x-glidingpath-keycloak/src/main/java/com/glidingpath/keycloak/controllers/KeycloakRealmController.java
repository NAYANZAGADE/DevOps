package com.glidingpath.keycloak.controllers;

import com.glidingpath.keycloak.dto.RealmCreationRequestDTO;
import com.glidingpath.keycloak.dto.RealmCreationResponseDTO;
import com.glidingpath.keycloak.service.KeycloakRealmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keycloak/realms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Keycloak Realm Management", description = "APIs for managing Keycloak realms")
public class KeycloakRealmController {

    private final KeycloakRealmService keycloakRealmService;

    /**
     * Endpoint to create a new organization realm in Keycloak.
     *
     * @param request RealmCreationRequestDTO containing organization details
     * @return ResponseEntity with RealmCreationResponseDTO containing creation status
     */
    @PostMapping("/organization")
    @Operation(summary = "Create organization realm", description = "Creates a new Keycloak realm for an organization with standard clients and roles")
    public ResponseEntity<RealmCreationResponseDTO> createOrganizationRealm(
            @Valid @RequestBody RealmCreationRequestDTO request) {
        
        log.info("Creating organization realm for: {}", request.getOrganizationName());
        RealmCreationResponseDTO response = keycloakRealmService.createOrganizationRealm(request);
        
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else if ("ALREADY_EXISTS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint to create the self-managed realm for orphan/unemployed participants.
     *
     * @return ResponseEntity with RealmCreationResponseDTO containing creation status
     */
    @PostMapping("/self-managed")
    @Operation(summary = "Create self-managed realm", description = "Creates the self-managed realm for orphan/unemployed participants")
    public ResponseEntity<RealmCreationResponseDTO> createSelfManagedRealm() {
        
        log.info("Creating self-managed realm");
        RealmCreationResponseDTO response = keycloakRealmService.createSelfManagedRealm();
        
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else if ("ALREADY_EXISTS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint to check if a realm exists in Keycloak.
     *
     * @param realmName the name of the realm to check
     * @return ResponseEntity with Boolean indicating realm existence
     */
    @GetMapping("/{realmName}/exists")
    @Operation(summary = "Check realm existence", description = "Checks if a realm exists")
    public ResponseEntity<Boolean> realmExists(@PathVariable String realmName) {
        boolean exists = keycloakRealmService.realmExists(realmName);
        return ResponseEntity.ok(exists);
    }

    /**
     * Endpoint to delete a realm from Keycloak.
     *
     * @param realmName the name of the realm to delete
     * @return ResponseEntity with no content indicating successful deletion
     */
    @DeleteMapping("/{realmName}")
    @Operation(summary = "Delete realm", description = "Deletes a realm (use with caution)")
    public ResponseEntity<Void> deleteRealm(@PathVariable String realmName) {
        keycloakRealmService.deleteRealm(realmName);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to initialize default realms (yatra, glidingpath, self_managed).
     *
     * @return ResponseEntity with String confirmation message
     */
    @PostMapping("/initialize")
    @Operation(summary = "Initialize default realms", description = "Creates the default realms (yatra, glidingpath, self_managed)")
    public ResponseEntity<String> initializeDefaultRealms() {
        String result = keycloakRealmService.initializeDefaultRealms();
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
} 