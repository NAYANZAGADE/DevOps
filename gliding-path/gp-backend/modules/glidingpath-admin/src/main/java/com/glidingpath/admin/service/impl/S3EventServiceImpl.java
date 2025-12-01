package com.glidingpath.admin.service.impl;

import com.glidingpath.admin.dto.AdminDocumentDTO;
import com.glidingpath.admin.service.S3EventService;
import com.glidingpath.admin.util.DocumentBuilderUtil;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import com.glidingpath.core.entity.Documents;
import com.glidingpath.core.repository.DocumentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3EventServiceImpl implements S3EventService {

	private final DocumentStatusRepository documentStatusRepository;

	// admin S3 key format: admin-documents/{tenantId}/...
	private static final Pattern FILE_KEY_PATTERN = Pattern.compile("^admin-documents/([^/]+)/.*$");

	@Override
	@Transactional
	public AdminDocumentDTO processS3ObjectCreated(String bucketName, String fileKey, Long fileSize, String contentType) {
		try {
			log.info("Processing S3 object created event for file: {}", fileKey);
			String tenantId = extractTenantIdFromFileKey(fileKey);
			if (tenantId == null) {
				log.warn("Could not extract tenant ID from file key: {}", fileKey);
				return null;
			}
			Documents document = DocumentBuilderUtil.createDocumentEntity(tenantId, "S3_EVENT");
			document.setFileKey(fileKey);
			Documents savedDocument = documentStatusRepository.save(document);
			log.info("Created document record for S3 file: {} with ID: {} and status: {}", fileKey, savedDocument.getId(), savedDocument.getStatus());
			return DocumentBuilderUtil.buildAdminDocumentDTO(savedDocument);
		} catch (Exception e) {
			log.error("Error processing S3 object created event for file: {}", fileKey, e);
			throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to process S3 object created event");
		}
	}

	@Override
	@Transactional
	public void processS3ObjectDeleted(String bucketName, String fileKey) {
		try {
			log.info("Processing S3 object deleted event for file: {}", fileKey);
			Documents document = documentStatusRepository.findByFileKey(fileKey).orElse(null);
			if (document != null) {
				documentStatusRepository.delete(document);
				log.info("Automatically deleted document record for S3 file: {} with ID: {}", fileKey, document.getId());
			} else {
				log.warn("No document record found for deleted S3 file: {}", fileKey);
			}
		} catch (Exception e) {
			log.error("Error processing S3 object deleted event for file: {}", fileKey, e);
			throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to process S3 object deleted event");
		}
	}

	@Override
	@Transactional
	public AdminDocumentDTO updateDocumentStatus(String fileKey, String status) {
		try {
			Documents document = documentStatusRepository.findByFileKey(fileKey)
				.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Document not found for file key: " + fileKey));
			document.setStatus(status);
			Documents updatedDocument = documentStatusRepository.save(document);
			log.info("Updated document status for file key: {} to: {}", fileKey, status);
			return DocumentBuilderUtil.buildAdminDocumentDTO(updatedDocument);
		} catch (Exception e) {
			log.error("Error updating document status for file key: {}", fileKey, e);
			throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to update document status");
		}
	}

	private String extractTenantIdFromFileKey(String fileKey) {
		Matcher matcher = FILE_KEY_PATTERN.matcher(fileKey);
		return matcher.matches() ? matcher.group(1) : null;
	}
} 