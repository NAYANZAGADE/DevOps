package com.glidingpath.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRegistrationDTO {
    private String tenantId;
    private List<EmployeeData> employees;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeData {
        private String email;
        private String firstName;
        private String lastName;
        private String username;
        private String password;
    }
} 