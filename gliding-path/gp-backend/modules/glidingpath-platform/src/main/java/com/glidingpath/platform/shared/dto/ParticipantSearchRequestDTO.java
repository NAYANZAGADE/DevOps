package com.glidingpath.platform.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantSearchRequestDTO {
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 10;
    private String search;
    private String tenantId;
}