package com.glidingpath.finch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinchReauthErrorResponse {
    private String timestamp;
    private String errorCode;
    private String path;
    private String description;
    private String reauthUrl;
    private String connectionId;
    private String customerId;
}
