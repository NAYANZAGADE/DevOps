package com.glidingpath.admin.util;

import com.glidingpath.admin.dto.AdminDocumentDTO;
import com.glidingpath.core.entity.Documents;

public class DocumentBuilderUtil {

    public static AdminDocumentDTO buildAdminDocumentDTO(Documents document) {
        return AdminDocumentDTO.builder()
            .id(document.getId())
            .tenantId(document.getTenantId())
            .fileKey(document.getFileKey())
            .status(document.getStatus())
            .createdBy(document.getCreatedBy())
            .createdAt(document.getCreatedAt())
            .build();
    }

    public static Documents createDocumentEntity(String tenantId, String uploadedBy) {
        Documents document = new Documents();
        document.setTenantId(tenantId);
        document.setStatus("ACTIVE");
        document.setCreatedBy(uploadedBy);
        return document;
    }
} 