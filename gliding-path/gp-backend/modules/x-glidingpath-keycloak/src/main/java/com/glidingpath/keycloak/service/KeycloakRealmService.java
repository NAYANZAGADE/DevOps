package com.glidingpath.keycloak.service;

import com.glidingpath.keycloak.dto.RealmCreationRequestDTO;
import com.glidingpath.keycloak.dto.RealmCreationResponseDTO;

public interface KeycloakRealmService {
    RealmCreationResponseDTO createOrganizationRealm(RealmCreationRequestDTO request);
    RealmCreationResponseDTO createSelfManagedRealm();
    boolean realmExists(String realmName);
    void deleteRealm(String realmName);
    String initializeDefaultRealms();
}
