package com.glidingpath.admin.util;

import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import com.glidingpath.platform.shared.dto.PresignedUrlRequest;

import java.util.Arrays;
import java.util.List;

public class DocumentValidationUtil {

    private static final List<String> VALID_STATUSES = Arrays.asList("ACTIVE", "COMPLETED", "INACTIVE", "DELETED");

    public static void validatePresignedUrlRequest(PresignedUrlRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Request cannot be null");
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "File name is required");
        }
        if (request.getContentType() == null || request.getContentType().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Content type is required");
        }
        if (request.getTenantId() == null || request.getTenantId().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Tenant ID is required");
        }
        if (!request.getFileName().toLowerCase().endsWith(".pdf")) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Only PDF files are allowed");
        }
        if (!"application/pdf".equals(request.getContentType())) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Only PDF content type is allowed");
        }
    }

    public static void validateTenantId(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Tenant ID is required");
        }
    }
} 