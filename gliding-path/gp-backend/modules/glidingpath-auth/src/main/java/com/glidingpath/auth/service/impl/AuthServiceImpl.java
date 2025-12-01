package com.glidingpath.auth.service.impl;

import com.glidingpath.auth.dto.EmployerRegistrationDTO;
import com.glidingpath.auth.dto.LoginRequestDTO;
import com.glidingpath.auth.dto.LoginResponseDTO;
import com.glidingpath.keycloak.dto.RealmCreationRequestDTO;
import com.glidingpath.auth.dto.TenantResolutionDTO;
import com.glidingpath.auth.dto.EmployeeRegistrationDTO;
import com.glidingpath.core.entity.Tenant;
import com.glidingpath.core.entity.User;
import com.glidingpath.core.repository.TenantRepository;
import com.glidingpath.core.repository.UserRepository;
import com.glidingpath.auth.service.AuthService;
import com.glidingpath.keycloak.service.KeycloakRealmService;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.keycloak.admin.client.CreatedResponseUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakRealmService keycloakRealmService;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    @Qualifier("keycloakJdbcTemplate")
    private final JdbcTemplate keycloakJdbcTemplate;
    
    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String keycloakBaseUrl;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            // Create a Keycloak client for the specific realm
            Keycloak userKeycloak = Keycloak.getInstance(
                keycloakBaseUrl,
                request.getRealm(),
                request.getUsername(),
                request.getPassword(),
                "web-client" // Using the standard web client
            );
            
            // Get access token
            AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();
            
            return LoginResponseDTO.builder()
                    .accessToken(tokenResponse.getToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType(tokenResponse.getTokenType())
                    .expiresIn((int) tokenResponse.getExpiresIn())
                    .scope(tokenResponse.getScope())
                    .build();
                    
        } catch (Exception e) {
            log.error("Login failed for user: {} in realm: {}", request.getUsername(), request.getRealm(), e);
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid credentials", e);
        }
    }

    @Override
    public TenantResolutionDTO resolveTenants(String email, String role) {
        try {
            // Query Keycloak database to find users with this email and role across all realms
            String sql = """
                SELECT DISTINCT
                    r.name as realm_name
                FROM user_entity u
                JOIN realm r ON u.realm_id = r.id
                JOIN user_role_mapping urm ON u.id = urm.user_id
                JOIN keycloak_role kr ON urm.role_id = kr.id
                WHERE u.email = ? AND kr.name = ?
                """;
            
            List<Map<String, Object>> results = keycloakJdbcTemplate.queryForList(sql, email, role);
            
            List<TenantResolutionDTO.TenantInfo> tenants = results.stream()
                    .map(row -> {
                        String realmName = (String) row.get("realm_name");
                        String orgId = realmName.startsWith("org_") ? realmName : "org_" + realmName;
                        String displayName = realmName.replace("org_", "").replace("_", " ");
                        
                        return TenantResolutionDTO.TenantInfo.builder()
                                .orgId(orgId)
                                .displayName(displayName)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            return TenantResolutionDTO.builder()
                    .email(email)
                    .tenants(tenants)
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to resolve tenants for email: {} with role: {}", email, role, e);
            throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to resolve tenants", e);
        }
    }

    @Override
    @Transactional
    public void registerEmployer(EmployerRegistrationDTO request) {
        try {
            // 1. Create realm first
            RealmCreationRequestDTO realmRequest = RealmCreationRequestDTO.builder()
                    .organizationName(request.getOrganizationName())
                    .organizationSlug(request.getOrganizationSlug())
                    .emailDomain(request.getEmailDomain())
                    .description(request.getDescription())
                    .build();
            
            keycloakRealmService.createOrganizationRealm(realmRequest);
            
            // 2. Create tenant in app database
            Tenant tenant = Tenant.builder()
                    .orgId("org_" + request.getOrganizationSlug())
                    .displayName(request.getOrganizationName())
                    .build();
            tenant = tenantRepository.save(tenant);
            
            // 3. Create user in Keycloak
            String realmName = "org_" + request.getOrganizationSlug();
            RealmResource realmResource = keycloakAdminClient.realm(realmName);
            UsersResource usersResource = realmResource.users();
            
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmailVerified(true);
            
            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            user.setCredentials(Arrays.asList(credential));
            
            // Create user
            var response = usersResource.create(user);
            String userId = CreatedResponseUtil.getCreatedId(response);
            
            // 4. Assign EMPLOYER role
            assignRoleToUser(realmResource, userId, "EMPLOYER");
            
            // 5. Create user in app database
            User appUser = User.builder()
                    .preferredUsername(request.getUsername())
                    .email(request.getEmail())
                    .sub(userId)
                    .tenant(tenant)
                    .build();
            userRepository.save(appUser);
            
            log.info("Employer registered successfully: {} in realm: {}", request.getUsername(), realmName);
            
            // TODO: Send welcome email and notification
            
        } catch (Exception e) {
            log.error("Failed to register employer: {}", request.getUsername(), e);
            throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to register employer", e);
        }
    }

    @Override
    @Transactional
    public void registerEmployees(EmployeeRegistrationDTO request) {
        try {
            // Get tenant
            Tenant tenant = tenantRepository.findByOrgId(request.getTenantId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Tenant not found: " + request.getTenantId()));
            
            String realmName = request.getTenantId();
            RealmResource realmResource = keycloakAdminClient.realm(realmName);
            UsersResource usersResource = realmResource.users();
            
            for (EmployeeRegistrationDTO.EmployeeData employee : request.getEmployees()) {
                try {
                    // Check if user already exists
                    if (userRepository.findByPreferredUsernameAndTenant_OrgId(employee.getUsername(), request.getTenantId()).isPresent()) {
                        log.info("Employee already exists, skipping: {} in tenant: {}", employee.getUsername(), request.getTenantId());
                        continue;
                    }
                    
                    // Create user in Keycloak
                    UserRepresentation user = new UserRepresentation();
                    user.setEnabled(true);
                    user.setUsername(employee.getUsername());
                    user.setEmail(employee.getEmail());
                    user.setFirstName(employee.getFirstName());
                    user.setLastName(employee.getLastName());
                    user.setEmailVerified(true);
                    
                    // Set password
                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(employee.getPassword());
                    credential.setTemporary(false);
                    user.setCredentials(Arrays.asList(credential));
                    
                    // Create user
                    var response = usersResource.create(user);
                    String userId = CreatedResponseUtil.getCreatedId(response);
                    
                    // Assign EMPLOYEE role
                    assignRoleToUser(realmResource, userId, "EMPLOYEE");
                    
                    // Create user in app database
                    User appUser = User.builder()
                            .preferredUsername(employee.getUsername())
                            .email(employee.getEmail())
                            .sub(userId)
                            .tenant(tenant)
                            .build();
                    userRepository.save(appUser);
                    
                    log.info("Employee registered successfully: {} in tenant: {}", employee.getUsername(), request.getTenantId());
                    
                } catch (Exception e) {
                    log.error("Failed to register employee: {} in tenant: {}", employee.getUsername(), request.getTenantId(), e);
                    // Continue with next employee instead of failing the entire batch
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to register employees for tenant: {}", request.getTenantId(), e);
            throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to register employees", e);
        }
    }



    private void assignRoleToUser(RealmResource realmResource, String userId, String roleName) {
        try {
            // Get the role
            var role = realmResource.roles().get(roleName).toRepresentation();
            
            // Assign role to user
            realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(role));
            
        } catch (Exception e) {
            log.error("Failed to assign role: {} to user: {}", roleName, userId, e);
            throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to assign role to user", e);
        }
    }
} 