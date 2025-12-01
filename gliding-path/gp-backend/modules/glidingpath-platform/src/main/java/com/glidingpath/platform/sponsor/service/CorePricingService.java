package com.glidingpath.platform.sponsor.service;

import com.glidingpath.platform.sponsor.dto.CorePricingDTO;

/**
 * Service interface for fetching core pricing data.
 */
public interface CorePricingService {

    /**
     * Fetches core pricing details.
     * @return CorePricingDTO with formatted pricing data.
     */
    CorePricingDTO getCorePricing(String tenantId);
} 