package com.glidingpath.platform.sponsor.service;

import java.time.Duration;

public interface S3Service {
	class PresignedUrlResponse {
		public final String uploadUrl;
		public final String fileKey;
		public final String bucketName;
		public final Duration expiration;
		public PresignedUrlResponse(String uploadUrl, String fileKey, String bucketName, Duration expiration) {
			this.uploadUrl = uploadUrl; this.fileKey = fileKey; this.bucketName = bucketName; this.expiration = expiration;
		}
	}
	PresignedUrlResponse generateUploadUrl(String fileName, String contentType, String tenantId);
	String generateDownloadUrl(String fileKey);
	void deleteFile(String fileKey);
	boolean fileExists(String fileKey);
} 