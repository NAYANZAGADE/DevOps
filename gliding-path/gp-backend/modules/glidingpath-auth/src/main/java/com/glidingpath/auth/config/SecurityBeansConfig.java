package com.glidingpath.auth.config;

import com.glidingpath.core.entity.User;
import com.glidingpath.core.repository.TenantRepository;
import com.glidingpath.core.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.function.Function;

@Configuration
public class SecurityBeansConfig {
    @Bean
    public Function<Jwt, User> fetchUser(UserRepository userRepository, TenantRepository tenantRepository) {
        return jwt -> {
            String username = jwt.getClaimAsString("preferred_username");
            String orgId = jwt.getClaimAsString("org_id");
            if (username == null || orgId == null) {
                throw new IllegalStateException("preferred_username or org_id claim not found in JWT");
            }
            return userRepository.findByPreferredUsernameAndTenant_OrgId(username, orgId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username + " in org: " + orgId));
        };
    }
} 