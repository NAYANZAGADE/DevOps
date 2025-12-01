package com.glidingpath.platform.sponsor.service;

import com.glidingpath.platform.shared.dto.TrusteeConfirmationRequestDTO;
import com.glidingpath.platform.shared.dto.PlanSignatureRequestDTO;
import com.glidingpath.platform.shared.dto.TrusteeConfirmationResponseDTO;
import com.glidingpath.platform.shared.dto.PlanSignatureResponseDTO;

public interface TrusteeSignatureService {
    TrusteeConfirmationResponseDTO createTrusteeConfirmation(String tenantId, TrusteeConfirmationRequestDTO request);
    PlanSignatureResponseDTO createPlanSignature(String tenantId, PlanSignatureRequestDTO request);
} 