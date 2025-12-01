package com.glidingpath.admin.controllers;

import com.glidingpath.admin.dto.DocumentStatusResponseDTO;
import com.glidingpath.admin.service.DocumentStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class DocumentController {

	private final DocumentStatusService documentStatusService;

	public DocumentController(DocumentStatusService documentStatusService) {
		this.documentStatusService = documentStatusService;
	}

	@GetMapping("/list")
	public ResponseEntity<DocumentStatusResponseDTO> getDocuments(@RequestParam("tenantId") String tenantId) {
		return ResponseEntity.ok(documentStatusService.getDocumentsByTenantId(tenantId));
	}
} 