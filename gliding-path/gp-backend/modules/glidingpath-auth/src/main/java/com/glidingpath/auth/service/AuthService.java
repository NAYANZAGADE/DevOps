package com.glidingpath.auth.service;

import com.glidingpath.auth.dto.EmployerRegistrationDTO;
import com.glidingpath.auth.dto.LoginRequestDTO;
import com.glidingpath.auth.dto.LoginResponseDTO;
import com.glidingpath.auth.dto.TenantResolutionDTO;
import com.glidingpath.auth.dto.EmployeeRegistrationDTO;

public interface AuthService {
    
    LoginResponseDTO login(LoginRequestDTO request);
    
    TenantResolutionDTO resolveTenants(String email, String role);
    
    void registerEmployer(EmployerRegistrationDTO request);
    
    void registerEmployees(EmployeeRegistrationDTO request);
} 