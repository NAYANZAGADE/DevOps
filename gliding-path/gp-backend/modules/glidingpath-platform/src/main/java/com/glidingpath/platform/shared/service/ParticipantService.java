package com.glidingpath.platform.shared.service;

import com.glidingpath.platform.shared.dto.ParticipantResponseDTO;
import com.glidingpath.platform.shared.dto.ParticipantSearchRequestDTO;
import org.springframework.data.domain.Page;

public interface ParticipantService {
    Page<ParticipantResponseDTO> getParticipants(ParticipantSearchRequestDTO request);
}