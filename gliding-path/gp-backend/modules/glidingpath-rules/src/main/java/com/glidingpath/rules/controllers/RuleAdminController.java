package com.glidingpath.rules.controllers;

import com.glidingpath.rules.config.DroolsConfig;
import com.glidingpath.rules.service.RuleAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/rules")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Rule Administration",
    description = "REST API endpoints for managing Drools business rules engine. " +
                 "Provides functionality to reload business rules from database without application restart. " +
                 "Supports dynamic rule updates for eligibility, pricing, and other business logic."
)
public class RuleAdminController {
    private final DroolsConfig droolsConfig;
    private final RuleAdminService ruleAdminService;
    
    @Autowired
    private ApplicationContext applicationContext;

    @PostMapping("/reload")
    @Operation(
        summary = "Reload business rules from database",
        description = "Dynamically reloads all business rules from the database into the Drools engine " +
                     "without requiring application restart. This allows for real-time rule updates " +
                     "including eligibility rules, pricing rules, and other business logic. " +
                     "The operation creates a new KieContainer and updates the Spring context.",
        operationId = "reloadRules"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rules reloaded successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Rules reloaded from database successfully"),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "Rules reloaded from database successfully",
                    summary = "Rules engine updated successfully"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Rule reload failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Failed to reload rules: Database connection error"),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "Failed to reload rules: Database connection error",
                    summary = "Rule reload operation failed"
                )
            )
        )
    })
    public ResponseEntity<String> reloadRules() throws Exception {
    	  String result = ruleAdminService.reloadRules();
          return ResponseEntity.ok(result);
    }
} 