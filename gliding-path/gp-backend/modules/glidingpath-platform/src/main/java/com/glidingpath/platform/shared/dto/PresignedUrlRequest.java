package com.glidingpath.platform.shared.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {
	private String fileName;
	private String contentType;
	private String tenantId;
} 