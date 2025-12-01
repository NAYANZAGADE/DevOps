package com.glidingpath.admin.service;

import com.glidingpath.admin.dto.AdminDocumentDTO;

public interface S3EventService {
	AdminDocumentDTO processS3ObjectCreated(String bucketName, String fileKey, Long fileSize, String contentType);
	void processS3ObjectDeleted(String bucketName, String fileKey);
	AdminDocumentDTO updateDocumentStatus(String fileKey, String status);
} 