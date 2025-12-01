package com.glidingpath.platform.shared.service.impl;

import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.repository.PlanParticipantRepository;
import com.glidingpath.platform.shared.dto.ParticipantResponseDTO;
import com.glidingpath.platform.shared.dto.ParticipantSearchRequestDTO;
import com.glidingpath.platform.shared.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantServiceImpl implements ParticipantService {
    
    private final PlanParticipantRepository planParticipantRepository;
    
    @Override
    public Page<ParticipantResponseDTO> getParticipants(ParticipantSearchRequestDTO request) {
        log.info("Fetching participants for tenantId: {}, page: {}, size: {}, search: {}", 
                request.getTenantId(), request.getPage(), request.getSize(), request.getSearch());
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<PlanParticipant> participants;
        
        if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {
            participants = planParticipantRepository.findByTenantIdAndNameContaining(
                    request.getTenantId(), request.getSearch(), pageable);
        } else {
            participants = planParticipantRepository.findByTenantId(request.getTenantId(), pageable);
        }
        
        return participants.map(this::mapToResponseDTO);
    }
    
    private ParticipantResponseDTO mapToResponseDTO(PlanParticipant participant) {
        return ParticipantResponseDTO.builder()
                .accountNumber(participant.getIndividualId())
                .fullName(participant.getFirstName()+" "+participant.getLastName())
                .status(participant.getEmploymentStatus())
                .portfolioType(mapPortfolioType(participant))
                .balance("Temp_TBD_BALANCE") // Temporary
                .vestedStatus("Temp_vested non") // Temporary
                .build();
    }
     
    private String mapPortfolioType(PlanParticipant participant) {
        if (participant.getDepartmentName() != null) {
            return participant.getDepartmentName();
        }
        return participant.getEmploymentType() != null ? participant.getEmploymentType() : "General";
    }
}