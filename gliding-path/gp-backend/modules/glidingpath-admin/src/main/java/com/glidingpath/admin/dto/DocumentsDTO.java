package com.glidingpath.admin.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentsDTO {
	private UUID id;
	private String tenantId;
	private String status;
	private String fileKey;
	private String uploadedBy;
	private LocalDateTime createdAt;
	private String downloadUrl;
} 