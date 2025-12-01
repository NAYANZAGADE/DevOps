package com.glidingpath.admin.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentStatusResponseDTO {
	private List<DocumentsDTO> documents;
} 