package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.auth.dto.EmployerRegistrationDTO;
import com.glidingpath.auth.dto.LoginRequestDTO;
import com.glidingpath.auth.dto.LoginResponseDTO;
import com.glidingpath.auth.dto.TenantResolutionDTO;
import com.glidingpath.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for authentication and user management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username/password and return JWT tokens")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login attempt for user: {} in realm: {}", request.getUsername(), request.getRealm());
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tenants/resolve")
    @Operation(summary = "Resolve tenants by email and role", description = "Find all tenants/organizations associated with an email address and specific role")
    public ResponseEntity<TenantResolutionDTO> resolveTenants(
            @RequestParam String email,
            @RequestParam String role) {
        log.info("Resolving tenants for email: {} with role: {}", email, role);
        TenantResolutionDTO response = authService.resolveTenants(email, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/employer")
    @Operation(summary = "Register employer", description = "Register a new employer with organization and create realm")
    public ResponseEntity<String> registerEmployer(@Valid @RequestBody EmployerRegistrationDTO request) {
        log.info("Employer registration for: {} in organization: {}", request.getUsername(), request.getOrganizationName());
        authService.registerEmployer(request);
        return ResponseEntity.ok("Employer registered successfully");
    }


} 