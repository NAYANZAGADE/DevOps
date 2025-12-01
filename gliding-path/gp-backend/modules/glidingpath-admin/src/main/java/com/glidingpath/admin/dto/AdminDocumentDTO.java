package com.glidingpath.admin.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDocumentDTO {
	private UUID id;
	private String tenantId;
	private String status;
	private String fileKey;
	private String createdBy;
	private LocalDateTime createdAt;
} 