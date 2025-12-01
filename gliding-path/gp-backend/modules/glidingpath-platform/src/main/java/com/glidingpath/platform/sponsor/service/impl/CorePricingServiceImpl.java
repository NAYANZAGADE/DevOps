package com.glidingpath.platform.sponsor.service.impl;

import com.glidingpath.platform.sponsor.dto.CorePricingDTO;
import com.glidingpath.platform.sponsor.service.CorePricingService;
import com.glidingpath.core.entity.CorePricingProjection;
import com.glidingpath.core.repository.TenantPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for retrieving core pricing using projection.
 */
@Service
@RequiredArgsConstructor
public class CorePricingServiceImpl implements CorePricingService {

    private final TenantPlanRepository tenantPlanRepository;
    
    @Override
    public CorePricingDTO getCorePricing(String tenantId) {
        CorePricingProjection projection = tenantPlanRepository.findCorePricingByTenantId(tenantId);
        
        if (projection == null) {
            throw new RuntimeException("No pricing data found for tenant: " + tenantId);
        }
        
        return new CorePricingDTO(
            String.valueOf(projection.getBaseFee()),
            String.valueOf(projection.getParticipantFee()),
            String.valueOf(projection.getEmployerAccountFee()),
            String.valueOf(projection.getEmployeeAccountFee())
        );
    }
}
