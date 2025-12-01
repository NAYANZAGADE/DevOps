package com.glidingpath.keycloak.service.impl;

import com.glidingpath.keycloak.config.KeycloakConfig;
import com.glidingpath.keycloak.dto.RealmCreationRequestDTO;
import com.glidingpath.keycloak.dto.RealmCreationResponseDTO;
import com.glidingpath.keycloak.service.KeycloakRealmService;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.*;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakRealmServiceImpl implements KeycloakRealmService {

	private final Keycloak keycloakAdminClient;

	@Override
	public RealmCreationResponseDTO createOrganizationRealm(RealmCreationRequestDTO request) {
		String realmName = KeycloakConfig.RealmConfig.REALM_PREFIX + request.getOrganizationSlug();
		
		try {
			// Check if realm already exists
			if (realmExists(realmName)) {
				// Ensure baseline clients and roles also exist for pre-existing realms (idempotent)
				RealmResource existingRealm = keycloakAdminClient.realm(realmName);
				createClients(existingRealm);
				createRoles(existingRealm);
				return RealmCreationResponseDTO.builder()
						.realmName(realmName)
						.organizationName(request.getOrganizationName())
						.status("ALREADY_EXISTS")
						.build();
			}
			
			RealmRepresentation realm = createRealmRepresentation(realmName, request);
			keycloakAdminClient.realms().create(realm);

			// Ensure required clients and roles exist in the new organization realm
			RealmResource realmResource = keycloakAdminClient.realm(realmName);
			createClients(realmResource);
			createRoles(realmResource);
			
			return RealmCreationResponseDTO.builder()
					.realmName(realmName)
					.organizationName(request.getOrganizationName())
					.status("CREATED")
					.createdAt(LocalDateTime.now())
					.build();
		} catch (Exception e) {
			throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to create realm: " + e.getMessage(), e);
		}
	}

	@Override
	public RealmCreationResponseDTO createSelfManagedRealm() {
		String realmName = KeycloakConfig.RealmConfig.SELF_MANAGED_REALM;
		
		try {
			if (realmExists(realmName)) {
				return RealmCreationResponseDTO.builder()
						.realmName(realmName)
						.organizationName("Self Managed")
						.status("ALREADY_EXISTS")
						.message("Self managed realm already exists")
						.createdAt(LocalDateTime.now())
						.build();
			}

			// Create realm for self-managed users
			RealmRepresentation realm = new RealmRepresentation();
			realm.setRealm(realmName);
			realm.setDisplayName("Self Managed");
			realm.setEnabled(KeycloakConfig.RealmConfig.REALM_ENABLED);
			realm.setRegistrationAllowed(true); // Allow self-registration for unemployed
			realm.setResetPasswordAllowed(KeycloakConfig.RealmConfig.RESET_PASSWORD_ALLOWED);
			realm.setRememberMe(KeycloakConfig.RealmConfig.REMEMBER_ME_ALLOWED);
			realm.setVerifyEmail(KeycloakConfig.RealmConfig.VERIFY_EMAIL);
			realm.setLoginWithEmailAllowed(KeycloakConfig.RealmConfig.LOGIN_WITH_EMAIL);
			
			keycloakAdminClient.realms().create(realm);
			
			RealmResource realmResource = keycloakAdminClient.realm(realmName);
			List<String> createdClients = createClients(realmResource);
			List<String> createdRoles = createRoles(realmResource);

			log.info("Successfully created self-managed realm: {}", realmName);
			
			return RealmCreationResponseDTO.builder()
					.realmName(realmName)
					.organizationName("Self Managed")
					.status("SUCCESS")
					.message("Self managed realm created successfully")
					.createdAt(LocalDateTime.now())
					.createdClients(createdClients)
					.createdRoles(createdRoles)
					.build();

		} catch (Exception e) {
			log.error("Failed to create self-managed realm: {}", realmName, e);
			throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to create self-managed realm: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean realmExists(String realmName) {
		try {
			RealmsResource realms = keycloakAdminClient.realms();
			return realms.findAll().stream()
					.anyMatch(realm -> realm.getRealm().equals(realmName));
		} catch (Exception e) {
			log.error("Error checking if realm exists: {}", realmName, e);
			return false;
		}
	}

	@Override
	public void deleteRealm(String realmName) {
		try {
			keycloakAdminClient.realm(realmName).remove();
			log.info("Successfully deleted realm: {}", realmName);
		} catch (Exception e) {
			log.error("Failed to delete realm: {}", realmName, e);
			throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to delete realm: " + e.getMessage(), e);
		}
	}

	private RealmRepresentation createRealmRepresentation(String realmName, RealmCreationRequestDTO request) {
		RealmRepresentation realm = new RealmRepresentation();
		realm.setRealm(realmName);
		realm.setDisplayName(request.getOrganizationName());
		realm.setEnabled(KeycloakConfig.RealmConfig.REALM_ENABLED);
		realm.setRegistrationAllowed(KeycloakConfig.RealmConfig.REGISTRATION_ALLOWED);
		realm.setResetPasswordAllowed(KeycloakConfig.RealmConfig.RESET_PASSWORD_ALLOWED);
		realm.setRememberMe(KeycloakConfig.RealmConfig.REMEMBER_ME_ALLOWED);
		realm.setVerifyEmail(KeycloakConfig.RealmConfig.VERIFY_EMAIL);
		realm.setLoginWithEmailAllowed(KeycloakConfig.RealmConfig.LOGIN_WITH_EMAIL);
		
		if (request.getDescription() != null) {
			realm.setDisplayNameHtml("<div class=\"kc-logo-text\"><span>" + request.getOrganizationName() + "</span></div>");
		}
		
		return realm;
	}

	private List<String> createClients(RealmResource realmResource) {
		List<String> createdClients = new ArrayList<>();
		String realmName = realmResource.toRepresentation().getRealm();
		try {
			// Create Web Client
			ClientRepresentation webClient = createWebClient();
			addOrgIdProtocolMapper(webClient, realmName);
			realmResource.clients().create(webClient);
			createdClients.add(KeycloakConfig.RealmConfig.WEB_CLIENT_ID);
			
			// Create Mobile Client
			ClientRepresentation mobileClient = createMobileClient();
			addOrgIdProtocolMapper(mobileClient, realmName);
			realmResource.clients().create(mobileClient);
			createdClients.add(KeycloakConfig.RealmConfig.MOBILE_CLIENT_ID);
			
			// Create CMS Client
			ClientRepresentation cmsClient = createCmsClient();
			addOrgIdProtocolMapper(cmsClient, realmName);
			realmResource.clients().create(cmsClient);
			createdClients.add(KeycloakConfig.RealmConfig.CMS_CLIENT_ID);
			
		} catch (Exception e) {
			log.error("Failed to create clients", e);
		}
		
		return createdClients;
	}

	private void addOrgIdProtocolMapper(ClientRepresentation client, String orgIdValue) {
		ProtocolMapperRepresentation orgIdMapper = new ProtocolMapperRepresentation();
		orgIdMapper.setName("org_id");
		orgIdMapper.setProtocol("openid-connect");
		orgIdMapper.setProtocolMapper("oidc-hardcoded-claim-mapper");
		orgIdMapper.setConfig(new java.util.HashMap<>() {{
			put("claim.name", "org_id");
			put("claim.value", orgIdValue);
			put("jsonType.label", "String");
			put("id.token.claim", "true");
			put("access.token.claim", "true");
			put("userinfo.token.claim", "true");
		}});
		if (client.getProtocolMappers() == null) {
			client.setProtocolMappers(new ArrayList<>());
		}
		client.getProtocolMappers().add(orgIdMapper);
	}

	private ClientRepresentation createWebClient() {
		ClientRepresentation client = new ClientRepresentation();
		client.setClientId(KeycloakConfig.RealmConfig.WEB_CLIENT_ID);
		client.setName("Web Client");
		client.setEnabled(true);
		client.setPublicClient(true);
		client.setStandardFlowEnabled(true);
		client.setDirectAccessGrantsEnabled(true);
		client.setServiceAccountsEnabled(false);
		client.setRedirectUris(Arrays.asList("http://localhost:3000/*", "https://localhost:3000/*"));
		client.setWebOrigins(Arrays.asList("http://localhost:3000", "https://localhost:3000"));
		return client;
	}

	private ClientRepresentation createMobileClient() {
		ClientRepresentation client = new ClientRepresentation();
		client.setClientId(KeycloakConfig.RealmConfig.MOBILE_CLIENT_ID);
		client.setName("Mobile Client");
		client.setEnabled(true);
		client.setPublicClient(true);
		client.setStandardFlowEnabled(true);
		client.setDirectAccessGrantsEnabled(true);
		client.setServiceAccountsEnabled(false);
		client.setRedirectUris(Arrays.asList("com.glidingpath://oauth2redirect/*"));
		return client;
	}

	private ClientRepresentation createCmsClient() {
		ClientRepresentation client = new ClientRepresentation();
		client.setClientId(KeycloakConfig.RealmConfig.CMS_CLIENT_ID);
		client.setName("CMS Client");
		client.setEnabled(true);
		client.setPublicClient(false);
		client.setStandardFlowEnabled(true);
		client.setDirectAccessGrantsEnabled(true);
		client.setServiceAccountsEnabled(true);
		client.setRedirectUris(Arrays.asList("http://localhost:8080/*"));
		return client;
	}

	private List<String> createRoles(RealmResource realmResource) {
		List<String> createdRoles = new ArrayList<>();
		
		try {
			// Create EMPLOYEE role
			RoleRepresentation employeeRole = new RoleRepresentation();
			employeeRole.setName(KeycloakConfig.RealmConfig.ROLE_EMPLOYEE);
			employeeRole.setDescription("Employee role for plan members");
			realmResource.roles().create(employeeRole);
			createdRoles.add(KeycloakConfig.RealmConfig.ROLE_EMPLOYEE);
			
			// Create EMPLOYER role
			RoleRepresentation employerRole = new RoleRepresentation();
			employerRole.setName(KeycloakConfig.RealmConfig.ROLE_EMPLOYER);
			employerRole.setDescription("Employer role for plan sponsors");
			realmResource.roles().create(employerRole);
			createdRoles.add(KeycloakConfig.RealmConfig.ROLE_EMPLOYER);
			
			// Create ADMIN role
			RoleRepresentation adminRole = new RoleRepresentation();
			adminRole.setName(KeycloakConfig.RealmConfig.ROLE_ADMIN);
			adminRole.setDescription("Administrator role for realm management");
			realmResource.roles().create(adminRole);
			createdRoles.add(KeycloakConfig.RealmConfig.ROLE_ADMIN);
			
		} catch (Exception e) {
			log.error("Failed to create roles", e);
		}
		
		return createdRoles;
	}

	private String createAdminUser(RealmResource realmResource, RealmCreationRequestDTO request) {
		try {
			UserRepresentation user = new UserRepresentation();
			user.setUsername(request.getAdminEmail());
			user.setEmail(request.getAdminEmail());
			user.setFirstName(request.getAdminFirstName());
			user.setLastName(request.getAdminLastName());
			user.setEnabled(true);
			user.setEmailVerified(true);
			
			// Create user
			var response = realmResource.users().create(user);
			String userId = CreatedResponseUtil.getCreatedId(response);
			
			// Set password
			CredentialRepresentation credential = new CredentialRepresentation();
			credential.setType(CredentialRepresentation.PASSWORD);
			credential.setValue("changeme"); // Temporary password
			credential.setTemporary(true);
			realmResource.users().get(userId).resetPassword(credential);
			
			// Assign admin role
			RoleRepresentation adminRole = realmResource.roles().get(KeycloakConfig.RealmConfig.ROLE_ADMIN).toRepresentation();
			realmResource.users().get(userId).roles().realmLevel().add(Arrays.asList(adminRole));
			
			return userId;
			
				} catch (Exception e) {
			log.error("Failed to create admin user", e);
			return null;
		}
	}

	@Override
	public String initializeDefaultRealms() {
		try {
			log.info("Initializing default realms");
			
			// Create org_yatra realm
			RealmCreationRequestDTO yatraRequest = RealmCreationRequestDTO.builder()
					.organizationName("Yatra")
					.organizationSlug("yatra")
					.emailDomain("yatra.com")
					.description("Yatra organization realm")
					.build();
			createOrganizationRealm(yatraRequest);
			
			// Create org_glidingpath realm
			RealmCreationRequestDTO glidingpathRequest = RealmCreationRequestDTO.builder()
					.organizationName("GlidingPath")
					.organizationSlug("glidingpath")
					.emailDomain("glidingpath.com")
					.description("GlidingPath organization realm")
					.build();
			createOrganizationRealm(glidingpathRequest);
			
			// Create self_managed realm
			createSelfManagedRealm();
			
			return "Default realms initialized successfully";
			
		} catch (Exception e) {
			log.error("Failed to initialize default realms", e);
			throw new AppException(ErrorCode.KEYCLOAK_ERROR, "Failed to initialize default realms: " + e.getMessage(), e);
		}
	}
}
