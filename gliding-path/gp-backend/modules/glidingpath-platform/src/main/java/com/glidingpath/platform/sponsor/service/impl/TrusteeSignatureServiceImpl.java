package com.glidingpath.platform.sponsor.service.impl;

import com.glidingpath.platform.shared.dto.TrusteeConfirmationRequestDTO;
import com.glidingpath.platform.shared.dto.PlanSignatureRequestDTO;
import com.glidingpath.platform.shared.dto.TrusteeConfirmationResponseDTO;
import com.glidingpath.platform.shared.dto.PlanSignatureResponseDTO;
import com.glidingpath.core.entity.TrusteeConfirmationEntity;
import com.glidingpath.core.entity.PlanSignatureEntity;
import com.glidingpath.core.repository.TrusteeConfirmationRepository;
import com.glidingpath.core.repository.PlanSignatureRepository;
import com.glidingpath.platform.sponsor.service.TrusteeSignatureService;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementation for trustee signature operations.
 * Handles business logic for trustee confirmation and plan signature creation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrusteeSignatureServiceImpl implements TrusteeSignatureService {

    private final TrusteeConfirmationRepository confirmationRepository;
    private final PlanSignatureRepository signatureRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TrusteeConfirmationResponseDTO createTrusteeConfirmation(String tenantId, TrusteeConfirmationRequestDTO request) {
        TrusteeConfirmationEntity existingConfirmation = confirmationRepository.findByTenantId(tenantId);
        if (existingConfirmation != null) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Trustee confirmation already exists for tenant: " + tenantId);
        }
        
        TrusteeConfirmationEntity entity = modelMapper.map(request, TrusteeConfirmationEntity.class);
        entity.setTenantId(tenantId);
        entity.setConfirmationTimestamp(LocalDateTime.now());
        
        TrusteeConfirmationEntity saved = confirmationRepository.save(entity);
        log.info("Successfully created trustee confirmation with ID: {}", saved.getId());
        
        // Map and return response
        return modelMapper.map(saved, TrusteeConfirmationResponseDTO.class);
    }

    @Override
    @Transactional
    public PlanSignatureResponseDTO createPlanSignature(String tenantId, PlanSignatureRequestDTO request) {
        PlanSignatureEntity existingSignature = signatureRepository.findByTenantId(tenantId);
        if (existingSignature != null) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Plan signature already exists for tenant: " + tenantId);
        }
        
        PlanSignatureEntity entity = modelMapper.map(request, PlanSignatureEntity.class);
        entity.setTenantId(tenantId);
        entity.setSignatureTimestamp(LocalDateTime.now());
        
        PlanSignatureEntity saved = signatureRepository.save(entity);
        return modelMapper.map(saved, PlanSignatureResponseDTO.class);
    }
} 