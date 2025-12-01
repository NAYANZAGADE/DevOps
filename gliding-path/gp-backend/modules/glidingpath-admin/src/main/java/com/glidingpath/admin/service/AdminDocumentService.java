package com.glidingpath.admin.service;

import com.glidingpath.admin.dto.AdminDocumentDTO;
import com.glidingpath.platform.shared.dto.PresignedUrlRequest;
import com.glidingpath.platform.shared.dto.PresignedUrlResponse;

import java.util.List;

public interface AdminDocumentService {
	PresignedUrlResponse generateUploadUrl(PresignedUrlRequest request);
	List<AdminDocumentDTO> getDocumentsByTenantId(String tenantId);
	void deleteDocumentsByTenantId(String tenantId);
} 