package com.glidingpath.platform.sponsor.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.glidingpath.platform.sponsor.service.S3Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	@Value("${aws.s3.presigned.url.expiration}")
	private int expirationSeconds;

	public S3ServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}

	@Override
	public PresignedUrlResponse generateUploadUrl(String fileName, String contentType, String tenantId) {
		String fileKey = String.format("admin-documents/%s/%s_%s_%s",
			tenantId,
			System.currentTimeMillis(),
			UUID.randomUUID(),
			fileName.replaceAll("[^a-zA-Z0-9.-]", "_")
		);
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofSeconds(expirationSeconds))
			.putObjectRequest(r -> r.bucket(bucketName).key(fileKey).contentType(contentType))
			.build();
		String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
		return new PresignedUrlResponse(presignedUrl, fileKey, bucketName, Duration.ofSeconds(expirationSeconds));
	}

	@Override
	public String generateDownloadUrl(String fileKey) {
		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofSeconds(expirationSeconds))
			.getObjectRequest(r -> r.bucket(bucketName).key(fileKey))
			.build();
		return s3Presigner.presignGetObject(presignRequest).url().toString();
	}

	@Override
	public void deleteFile(String fileKey) {
		DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(bucketName).key(fileKey).build();
		s3Client.deleteObject(deleteRequest);
	}

	@Override
	public boolean fileExists(String fileKey) {
		try {
			HeadObjectRequest head = HeadObjectRequest.builder().bucket(bucketName).key(fileKey).build();
			s3Client.headObject(head);
			return true;
		} catch (NoSuchKeyException e) {
			return false;
		}
	}
} 