package com.glidingpath.platform.sponsor.service;


import com.glidingpath.common.dto.CommonEmailRequest;
import com.glidingpath.common.dto.EmailRequestDTO;

public interface EmailService {
	boolean sendEmail(EmailRequestDTO request);
	
	default boolean sendEmail(CommonEmailRequest request) {
		if (request == null) return false;
		EmailRequestDTO dto = new EmailRequestDTO();
		dto.setTo(request.getTo());
		dto.setSubject(request.getSubject());
		dto.setTemplateName(request.getTemplateName());
		dto.setVariables(request.getVariables());
		return sendEmail(dto);
	}
} 