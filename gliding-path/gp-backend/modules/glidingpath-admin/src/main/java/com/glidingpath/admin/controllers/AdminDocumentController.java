package com.glidingpath.admin.controllers;

import com.glidingpath.admin.dto.AdminDocumentDTO;
import com.glidingpath.admin.service.AdminDocumentService;
import com.glidingpath.platform.shared.dto.PresignedUrlRequest;
import com.glidingpath.platform.shared.dto.PresignedUrlResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/documents")
public class AdminDocumentController {

	private final AdminDocumentService adminDocumentService;

	public AdminDocumentController(AdminDocumentService adminDocumentService) {
		this.adminDocumentService = adminDocumentService;
	}

	@PostMapping("/presigned-url")
	public ResponseEntity<PresignedUrlResponse> generateUploadUrl(@RequestBody PresignedUrlRequest request) {
		PresignedUrlResponse response = adminDocumentService.generateUploadUrl(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/tenant/{tenantId}")
	public ResponseEntity<List<AdminDocumentDTO>> getDocumentsByTenant(@PathVariable String tenantId) {
		List<AdminDocumentDTO> documents = adminDocumentService.getDocumentsByTenantId(tenantId);
		return ResponseEntity.ok(documents);
	}

	@DeleteMapping("/tenant/{tenantId}")
	public ResponseEntity<Void> deleteDocumentsByTenant(@PathVariable String tenantId) {
		adminDocumentService.deleteDocumentsByTenantId(tenantId);
		return ResponseEntity.noContent().build();
	}
} 