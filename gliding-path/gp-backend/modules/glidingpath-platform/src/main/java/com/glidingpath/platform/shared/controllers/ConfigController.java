package com.glidingpath.platform.shared.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.glidingpath.core.entity.SystemConfiguration;
import com.glidingpath.core.repository.SystemConfigurationRepository;
import com.glidingpath.platform.sponsor.service.ConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
@Tag(name = "System Configuration", description = "APIs for retrieving system-wide configuration settings")
public class ConfigController {
    private final ConfigService configService;

    /**
     * Endpoint to retrieve common system configuration.
     *
     * @return ResponseEntity with JsonNode containing configuration data
     */
    @GetMapping
    @Operation(summary = "Get system configuration", description = "Retrieves active system configuration including application settings and feature flags")
    public ResponseEntity<JsonNode> getCommonConfig() {
        return ResponseEntity.ok(configService.getCommonConfig());
    }
} 