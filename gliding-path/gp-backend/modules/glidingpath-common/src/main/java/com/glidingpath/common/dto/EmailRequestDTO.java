package com.glidingpath.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Email request for sending templated emails via Brevo service",
    example = """
    {
      "to": "john.doe@company.com",
      "subject": "Welcome to GlidingPath Benefits!",
      "templateName": "welcome.html",
      "variables": {
        "firstName": "John",
        "companyName": "TechCorp Inc",
        "benefitsStartDate": "2024-01-15",
        "enrollmentDeadline": "2024-02-15"
      }
    }
    """
)
public class EmailRequestDTO {
    
    @Schema(
        description = "Recipient email address",
        example = "john.doe@company.com",
        required = true,
        pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    )
    private String to;
    
    @Schema(
        description = "Email subject line",
        example = "Welcome to GlidingPath Benefits!",
        required = true,
        minLength = 1,
        maxLength = 255
    )
    private String subject;
    
    @Schema(
        description = "Email template name (e.g., 'welcome.html', 'eligibility-notification.html')",
        example = "welcome.html",
        required = true,
        allowableValues = {"welcome.html", "eligibility-notification.html", "benefits-reminder.html"}
    )
    private String templateName;
    
    @Schema(
        description = "Dynamic variables to render into the email template. Key-value pairs where the value is a string.",
        example = "{ \"firstName\": \"John\", \"companyName\": \"TechCorp Inc\" }",
        required = false
    )
    private Map<String, Object> variables;
}