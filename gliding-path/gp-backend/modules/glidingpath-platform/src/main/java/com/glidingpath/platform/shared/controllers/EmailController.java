package com.glidingpath.platform.shared.controllers;

import com.glidingpath.common.dto.EmailRequestDTO;
import com.glidingpath.platform.sponsor.service.EmailService;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Email Service",
    description = "REST API endpoints for sending templated emails via Brevo service with retry mechanism and comprehensive error handling"
)
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(
        summary = "Send templated email",
        description = "Sends a templated email using the Brevo email service with resilience4j retry mechanism. " +
                     "Supports dynamic content replacement and handles various email templates including welcome emails, " +
                     "eligibility notifications, and other business communications. The service includes automatic retry " +
                     "logic for improved reliability and comprehensive error handling.",
        operationId = "sendEmail"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Email sent successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Email sent successfully"),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "Email sent successfully",
                    summary = "Email delivered successfully"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Invalid email request: recipient email is required"),
                examples = @ExampleObject(
                    name = "Bad Request",
                    value = "Invalid email request: recipient email is required",
                    summary = "Validation error in request"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Email service error or retry failure",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Email failed to send after retries"),
                examples = @ExampleObject(
                    name = "Service Error",
                    value = "Email failed to send after retries",
                    summary = "Email service unavailable or retry limit exceeded"
                )
            )
        )
    })
    public ResponseEntity<String> sendEmail(
        @Parameter(
            description = "Email request containing recipient, template, and dynamic content",
            required = true,
            content = @Content(
                schema = @Schema(implementation = EmailRequestDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Welcome Email",
                        value = """
                        {
                          \"to\": \"john.doe@company.com\",
                          \"subject\": \"Welcome to GlidingPath Benefits!\",
                          \"templateName\": \"welcome.html\",
                          \"variables\": {
                            \"firstName\": \"John\",
                            \"companyName\": \"TechCorp Inc\",
                            \"benefitsStartDate\": \"2024-01-15\"
                          }
                        }
                        """,
                        summary = "Welcome email with dynamic content"
                    ),
                    @ExampleObject(
                        name = "Eligibility Notification",
                        value = """
                        {
                          \"to\": \"jane.smith@company.com\",
                          \"subject\": \"You're Eligible for Benefits!\",
                          \"templateName\": \"eligibility-notification.html\",
                          \"variables\": {
                            \"employeeName\": \"Jane Smith\",
                            \"planType\": \"401(k) with Employer Match\",
                            \"eligibilityDate\": \"2024-02-01\",
                            \"nextSteps\": \"Complete enrollment within 30 days\"
                          }
                        }
                        """,
                        summary = "Eligibility notification email"
                    )
                }
            )
        ) @RequestBody EmailRequestDTO request
    ) {
        log.info("Received email request for recipient: {}", request.getTo());
        try {
            boolean emailSent = emailService.sendEmail(request);
            if (emailSent) {
                log.info("Email sent successfully to: {}", request.getTo());
                return ResponseEntity.ok("Email sent successfully");
            } else {
                log.error("Email failed to send after retries for recipient: {}", request.getTo());
                return ResponseEntity.status(500).body("Email failed to send after retries");
            }
        } catch (Exception e) {
            log.error("Unexpected error while sending email to {}: {}", request.getTo(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Email service error: " + e.getMessage());
        }
    }
} 