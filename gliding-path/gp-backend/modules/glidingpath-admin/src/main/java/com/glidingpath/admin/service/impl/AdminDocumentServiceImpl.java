package com.glidingpath.admin.service.impl;

import com.glidingpath.admin.dto.AdminDocumentDTO;
import com.glidingpath.admin.service.AdminDocumentService;
import com.glidingpath.admin.util.DocumentBuilderUtil;
import com.glidingpath.admin.util.DocumentValidationUtil;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import com.glidingpath.core.entity.Documents;
import com.glidingpath.core.repository.DocumentStatusRepository;
import com.glidingpath.platform.shared.dto.PresignedUrlRequest;
import com.glidingpath.platform.shared.dto.PresignedUrlResponse;
import com.glidingpath.platform.sponsor.service.S3Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDocumentServiceImpl implements AdminDocumentService {

	private final DocumentStatusRepository documentStatusRepository;
	private final S3Service s3Service;

	public AdminDocumentServiceImpl(DocumentStatusRepository documentStatusRepository, S3Service s3Service) {
		this.documentStatusRepository = documentStatusRepository;
		this.s3Service = s3Service;
	}

	@Override
	public PresignedUrlResponse generateUploadUrl(PresignedUrlRequest request) {
		DocumentValidationUtil.validatePresignedUrlRequest(request);
		S3Service.PresignedUrlResponse s3Response = s3Service.generateUploadUrl(
			request.getFileName(),
			request.getContentType(),
			request.getTenantId()
		);
		return PresignedUrlResponse.builder()
			.uploadUrl(s3Response.uploadUrl)
			.fileKey(s3Response.fileKey)
			.bucketName(s3Response.bucketName)
			.expiration(s3Response.expiration)
			.build();
	}

	@Override
	public List<AdminDocumentDTO> getDocumentsByTenantId(String tenantId) {
		List<Documents> documents = documentStatusRepository.findAllByTenantId(tenantId);
		return documents.stream()
			.map(DocumentBuilderUtil::buildAdminDocumentDTO)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteDocumentsByTenantId(String tenantId) {
		try {
			DocumentValidationUtil.validateTenantId(tenantId);
			List<Documents> documents = documentStatusRepository.findAllByTenantId(tenantId);
			if (documents.isEmpty()) {
				return;
			}
			for (Documents document : documents) {
				if (document.getFileKey() != null) {
					try {
						s3Service.deleteFile(document.getFileKey());
					} catch (Exception e) {
						// continue
					}
				}
			}
			documentStatusRepository.deleteByTenantId(tenantId);
		} catch (Exception e) {
			throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to delete documents for tenant: " + tenantId);
		}
	}
} 