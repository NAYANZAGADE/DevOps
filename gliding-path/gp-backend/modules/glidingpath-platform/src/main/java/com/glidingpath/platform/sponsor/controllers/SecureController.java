package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.core.entity.User;
import com.glidingpath.auth.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secure")
public class SecureController {

    @GetMapping("/me")
    public String me(@CurrentUser User user) {
        return "Hello " + user.getPreferredUsername();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly(@CurrentUser User user) {
        return "Admin access granted for " + user.getPreferredUsername();
    }

    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String employerOnly(@CurrentUser User user) {
    	return "Employer access granted for " + user.getPreferredUsername();
        
    }

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String employeeOnly(@CurrentUser User user) {
        return "Employee access granted for " + user.getPreferredUsername();
    }
} 