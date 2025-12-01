package com.glidingpath.platform.shared.dto;

import lombok.*;
import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {
	private String uploadUrl;
	private String fileKey;
	private String bucketName;
	private Duration expiration;
} 