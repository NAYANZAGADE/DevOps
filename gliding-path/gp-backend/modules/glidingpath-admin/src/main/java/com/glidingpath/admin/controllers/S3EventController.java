package com.glidingpath.admin.controllers;

import com.glidingpath.admin.dto.AdminDocumentDTO;
import com.glidingpath.admin.service.S3EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/s3-events")
@RequiredArgsConstructor
@Tag(name = "S3 Event Processing", description = "APIs for handling S3 event notifications")
public class S3EventController {

	private final S3EventService s3EventService;

	@PostMapping("/webhook")
	@Operation(summary = "Process S3 event notifications", description = "Handle S3 object created/deleted events")
	public ResponseEntity<String> processS3Event(@RequestBody Map<String, Object> event) {
		try {
			log.info("Received S3 event: {}", event);
			String eventName = extractEventName(event);
			String bucketName = extractBucketName(event);
			String fileKey = extractFileKey(event);
			Long fileSize = extractFileSize(event);
			String contentType = extractContentType(event);

			if ("ObjectCreated:Put".equals(eventName)) {
				AdminDocumentDTO document = s3EventService.processS3ObjectCreated(bucketName, fileKey, fileSize, contentType);
				log.info("Successfully processed S3 object created event for file: {}", fileKey);
				return ResponseEntity.ok("Document created successfully: " + (document != null ? document.getId() : "N/A"));
			} else if ("ObjectRemoved:Delete".equals(eventName)) {
				s3EventService.processS3ObjectDeleted(bucketName, fileKey);
				log.info("Successfully processed S3 object deleted event for file: {}", fileKey);
				return ResponseEntity.ok("Document deleted successfully");
			} else {
				log.info("Ignoring S3 event: {}", eventName);
				return ResponseEntity.ok("Event ignored");
			}
		} catch (Exception e) {
			log.error("Error processing S3 event", e);
			return ResponseEntity.internalServerError().body("Error processing S3 event: " + e.getMessage());
		}
	}

	private String extractEventName(Map<String, Object> event) {
		try {
			@SuppressWarnings("unchecked")
			java.util.List<Map<String, Object>> records = (java.util.List<Map<String, Object>>) event.get("Records");
			if (records != null && !records.isEmpty()) {
				Map<String, Object> firstRecord = records.get(0);
				if (firstRecord != null) {
					return (String) firstRecord.get("eventName");
				}
			}
		} catch (Exception e) {
			log.warn("Could not extract event name from S3 event", e);
		}
		return "Unknown";
	}

	private String extractBucketName(Map<String, Object> event) {
		try {
			@SuppressWarnings("unchecked")
			java.util.List<Map<String, Object>> records = (java.util.List<Map<String, Object>>) event.get("Records");
			if (records != null && !records.isEmpty()) {
				Map<String, Object> firstRecord = records.get(0);
				@SuppressWarnings("unchecked")
				Map<String, Object> s3 = (Map<String, Object>) firstRecord.get("s3");
				if (s3 != null) {
					@SuppressWarnings("unchecked")
					Map<String, Object> bucket = (Map<String, Object>) s3.get("bucket");
					if (bucket != null) {
						return (String) bucket.get("name");
					}
				}
			}
		} catch (Exception e) {
			log.warn("Could not extract bucket name from S3 event", e);
		}
		return "unknown-bucket";
	}

	private String extractFileKey(Map<String, Object> event) {
		try {
			@SuppressWarnings("unchecked")
			java.util.List<Map<String, Object>> records = (java.util.List<Map<String, Object>>) event.get("Records");
			if (records != null && !records.isEmpty()) {
				Map<String, Object> firstRecord = records.get(0);
				@SuppressWarnings("unchecked")
				Map<String, Object> s3 = (Map<String, Object>) firstRecord.get("s3");
				if (s3 != null) {
					@SuppressWarnings("unchecked")
					Map<String, Object> object = (Map<String, Object>) s3.get("object");
					if (object != null) {
						return (String) object.get("key");
					}
				}
			}
		} catch (Exception e) {
			log.warn("Could not extract file key from S3 event", e);
		}
		return "unknown-key";
	}

	private Long extractFileSize(Map<String, Object> event) {
		try {
			@SuppressWarnings("unchecked")
			java.util.List<Map<String, Object>> records = (java.util.List<Map<String, Object>>) event.get("Records");
			if (records != null && !records.isEmpty()) {
				Map<String, Object> firstRecord = records.get(0);
				@SuppressWarnings("unchecked")
				Map<String, Object> s3 = (Map<String, Object>) firstRecord.get("s3");
				if (s3 != null) {
					@SuppressWarnings("unchecked")
					Map<String, Object> object = (Map<String, Object>) s3.get("object");
					if (object != null) {
						Object size = object.get("size");
						if (size instanceof Number) {
							return ((Number) size).longValue();
						}
					}
				}
			}
		} catch (Exception e) {
			log.warn("Could not extract file size from S3 event", e);
		}
		return 0L;
	}

	private String extractContentType(Map<String, Object> event) {
		try {
			@SuppressWarnings("unchecked")
			java.util.List<Map<String, Object>> records = (java.util.List<Map<String, Object>>) event.get("Records");
			if (records != null && !records.isEmpty()) {
				Map<String, Object> firstRecord = records.get(0);
				@SuppressWarnings("unchecked")
				Map<String, Object> s3 = (Map<String, Object>) firstRecord.get("s3");
				if (s3 != null) {
					@SuppressWarnings("unchecked")
					Map<String, Object> object = (Map<String, Object>) s3.get("object");
					if (object != null) {
						return (String) object.get("contentType");
					}
				}
			}
		} catch (Exception e) {
			log.warn("Could not extract content type from S3 event", e);
		}
		return "application/pdf";
	}
} 