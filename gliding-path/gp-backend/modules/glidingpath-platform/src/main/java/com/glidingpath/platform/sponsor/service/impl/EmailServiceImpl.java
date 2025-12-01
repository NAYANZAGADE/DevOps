package com.glidingpath.platform.sponsor.service.impl;


import constants.SystemConstants;
import com.glidingpath.platform.sponsor.service.EmailService;
import com.glidingpath.platform.sponsor.service.NotificationService;
import com.glidingpath.common.dto.CommonEmailRequest;
import com.glidingpath.common.dto.EmailRequestDTO;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService, NotificationService {

	private final TemplateEngine templateEngine;
	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${brevo.api.key}")
	private String apiKey;
	
	@Value("${email.mock.enabled:true}")
	private boolean mockEnabled;

	@Override
	@Retry(name = "emailRetry", fallbackMethod = "sendEmailFallback")
	public boolean sendEmail(EmailRequestDTO request) {
		if (mockEnabled) {
			return sendMockEmail(request.getTo(), request.getSubject(), request.getTemplateName(), request.getVariables());
		}
		try {
			String htmlContent = renderTemplate(request.getTemplateName(), request.getVariables());
			HttpEntity<Map<String, Object>> httpRequest = buildBrevoRequest(request.getTo(), request.getSubject(), htmlContent, request.getVariables());
			ResponseEntity<String> response = restTemplate.postForEntity(SystemConstants.Email.BREVO_API_URL, httpRequest, String.class);
			return handleResponse(request.getTo(), request.getSubject(), response);
		} catch (Exception ex) {
			log.error("Failed to send email", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean sendEmail(CommonEmailRequest request) {
		if (request == null) return false;
		EmailRequestDTO dto = new EmailRequestDTO();
		dto.setTo(request.getTo());
		dto.setSubject(request.getSubject());
		dto.setTemplateName(request.getTemplateName());
		dto.setVariables(request.getVariables() == null ? new HashMap<>() : request.getVariables());
		return sendEmail(dto);
	}

	private boolean sendMockEmail(String to, String subject, String templateName, Map<String, Object> variables) {
		try {
			String htmlContent = renderTemplate(templateName, variables);
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			log.info("=== MOCK EMAIL SENT ===");
			log.info("Timestamp: {}", timestamp);
			log.info("To: {}", to);
			log.info("Subject: {}", subject);
			log.info("Template: {}", templateName);
			log.info("Variables: {}", variables);
			log.info("HTML Content:");
			log.info("{}", htmlContent);
			log.info("=== END MOCK EMAIL ===");
			return true;
		} catch (Exception ex) {
			log.error("Failed to send mock email", ex);
			return false;
		}
	}

	// Fallback method for Resilience4j retry
	public boolean sendEmailFallback(EmailRequestDTO request, Throwable t) {
		log.warn("Email failed after retries. Fallback executed. Reason: {}", t.getMessage());
		return sendMockEmail(request.getTo(), request.getSubject(), request.getTemplateName(), request.getVariables());
	}

	private String renderTemplate(String templateName, Map<String, Object> variables) throws Exception {
		String templatePath = SystemConstants.Email.TEMPLATE_DIR + templateName + SystemConstants.Email.TEMPLATE_SUFFIX;
		InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
		String rawTemplate = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
		Context context = new Context();
		variables.forEach(context::setVariable);
		return templateEngine.process(templateName, context);
	}

	private boolean handleResponse(String to, String subject, ResponseEntity<String> response) {
		boolean success = response.getStatusCode().is2xxSuccessful();
		if (success) {
			log.info("Email sent to {} with subject '{}'. Status: {}", to, subject, response.getStatusCode());
			log.debug("Brevo API Response: {}", response.getBody());
		} else {
			log.error("Email failed to send. Status: {}", response.getStatusCode());
		}
		return success;
	}

	private HttpEntity<Map<String, Object>> buildBrevoRequest(String to, String subject, String htmlContent, Map<String, Object> variables) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("sender", Map.of(
				"name", SystemConstants.Email.SENDER_NAME,
				"email", SystemConstants.Email.SENDER_EMAIL
		));
		payload.put("to", List.of(Map.of(
				"email", to,
				"name", variables.getOrDefault("name", "")
		)));
		payload.put("subject", subject);
		payload.put("htmlContent", htmlContent);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(SystemConstants.Email.HEADER_API_KEY, apiKey);

		return new HttpEntity<>(payload, headers);
	}
}
