package com.glidingpath.finch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyncResponseDTO<T> {
    
    @Builder.Default
    private String status = "SUCCESS";
    
    @Builder.Default
    private String message = "Operation completed successfully";
    
    private LocalDateTime timestamp;
    
    private SyncSummary summary;
    
    private List<T> data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncSummary {
        private int totalProcessed;
        private int newRecords;
        private int existingRecords;
        private int failedRecords;
        private String tenantId;
    }
    
    private static <T> SyncResponseDTO<T> createResponse(String status, String message, List<T> data, SyncSummary summary) {
        return SyncResponseDTO.<T>builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .summary(summary)
                .build();
    }
    
    public static <T> SyncResponseDTO<T> success(String message, List<T> data, SyncSummary summary) {
        return createResponse("SUCCESS", message, data, summary);
    }
    
    public static <T> SyncResponseDTO<T> noNewRecords(String message, SyncSummary summary) {
        return SyncResponseDTO.<T>builder()
                .status("SUCCESS")
                .message(message)
                .timestamp(LocalDateTime.now())
                .summary(summary)
                .build();
    }
    
    public static <T> SyncResponseDTO<T> error(String message, SyncSummary summary) {
        return createResponse("ERROR", message, null, summary);
    }
} 