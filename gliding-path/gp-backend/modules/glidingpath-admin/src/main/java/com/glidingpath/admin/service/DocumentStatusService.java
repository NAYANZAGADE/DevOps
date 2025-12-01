package com.glidingpath.admin.service;

import com.glidingpath.admin.dto.DocumentStatusResponseDTO;

public interface DocumentStatusService {
	DocumentStatusResponseDTO getDocumentsByTenantId(String tenantId);
} 