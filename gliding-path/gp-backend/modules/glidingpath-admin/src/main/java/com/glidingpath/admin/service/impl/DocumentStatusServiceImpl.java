package com.glidingpath.admin.service.impl;

import com.glidingpath.admin.dto.DocumentStatusResponseDTO;
import com.glidingpath.admin.dto.DocumentsDTO;
import com.glidingpath.admin.service.DocumentStatusService;
import com.glidingpath.core.entity.Documents;
import com.glidingpath.core.repository.DocumentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentStatusServiceImpl implements DocumentStatusService {

	private final DocumentStatusRepository documentStatusRepository;

	@Autowired(required = false)
	private S3Presigner s3Presigner;

	@Value("${aws.s3.bucket:}")
	private String bucketName;

	@Value("${aws.s3.presigned.url.expiration:900}")
	private int expirationSeconds;

	@Override
	public DocumentStatusResponseDTO getDocumentsByTenantId(String tenantId) {
		List<Documents> documents = documentStatusRepository.findAllByTenantId(tenantId);
		List<DocumentsDTO> dtoList = documents.stream()
			.map(this::buildDocumentsDTO)
			.collect(Collectors.toList());
		return DocumentStatusResponseDTO.builder().documents(dtoList).build();
	}

	private DocumentsDTO buildDocumentsDTO(Documents document) {
		DocumentsDTO dto = new DocumentsDTO();
		dto.setId(document.getId());
		dto.setTenantId(document.getTenantId());
		dto.setStatus(document.getStatus());
		dto.setFileKey(document.getFileKey());
		dto.setUploadedBy(document.getCreatedBy());
		dto.setCreatedAt(document.getCreatedAt());

		if (s3Presigner != null && bucketName != null && !bucketName.isBlank() && document.getFileKey() != null) {
			try {
				GetObjectPresignRequest req = GetObjectPresignRequest.builder()
					.signatureDuration(Duration.ofSeconds(expirationSeconds))
					.getObjectRequest(r -> r.bucket(bucketName).key(document.getFileKey()))
					.build();
				String url = s3Presigner.presignGetObject(req).url().toString();
				dto.setDownloadUrl(url);
			} catch (Exception e) {
				log.warn("Failed to generate download URL for fileKey={}", document.getFileKey(), e);
			}
		}
		return dto;
	}
} 