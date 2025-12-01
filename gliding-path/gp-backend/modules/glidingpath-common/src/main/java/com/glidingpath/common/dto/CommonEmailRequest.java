package com.glidingpath.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CommonEmailRequest {
	private String to;
	private String subject;
	private String templateName;
	private Map<String, Object> variables;
} 