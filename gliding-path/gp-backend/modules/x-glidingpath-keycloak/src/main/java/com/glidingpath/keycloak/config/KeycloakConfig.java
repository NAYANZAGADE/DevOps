package com.glidingpath.keycloak.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String authServerUrl;

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    @Value("${keycloak.admin.realm:master}")
    private String adminRealm;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(adminRealm)
                .clientId(adminClientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    /**
     * Common realm configuration for all organization realms
     */
    public static class RealmConfig {
        public static final String REALM_PREFIX = "org_";
        public static final String SELF_MANAGED_REALM = "self_managed";
        
        // Common roles across all realms
        public static final String ROLE_EMPLOYEE = "EMPLOYEE";
        public static final String ROLE_EMPLOYER = "EMPLOYER";
        public static final String ROLE_ADMIN = "ADMIN";
        
        // Common client configurations
        public static final String WEB_CLIENT_ID = "web-client";
        public static final String MOBILE_CLIENT_ID = "mobile-client";
        public static final String CMS_CLIENT_ID = "cms-client";
        
        // Default realm settings
        public static final boolean REALM_ENABLED = true;
        public static final boolean REGISTRATION_ALLOWED = false; // Only via invitation
        public static final boolean RESET_PASSWORD_ALLOWED = true;
        public static final boolean REMEMBER_ME_ALLOWED = true;
        public static final boolean VERIFY_EMAIL = true;
        public static final boolean LOGIN_WITH_EMAIL = true;
    }
} 