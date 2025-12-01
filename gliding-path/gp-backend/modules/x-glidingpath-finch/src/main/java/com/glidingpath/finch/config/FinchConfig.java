package com.glidingpath.finch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "finch")
public class FinchConfig {
    private String clientId;
    private String clientSecret;
    private String secretKey; // Finch API secret key for Bearer auth
    private String redirectUri;
    private String baseUrl;
    private String products;
    private String tokenUrl;
    private String sessionUrl; // Finch Connect sessions endpoint
    private String reauthUrl; // Finch reauthentication endpoint
    private String apiVersion; // Finch API version for headers
}
