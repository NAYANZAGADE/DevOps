package com.glidingpath.finch.service;

import com.glidingpath.finch.dto.FinchCompanyDetailsDTO;

public interface FinchDataFetcherService {
    /**
     * Fetches and syncs company data from Finch for the given tenant.
     * @param tenantId The unique identifier for the tenant.
     * @return The company details response DTO.
     * @throws Exception if the sync or API call fails.
     */
    FinchCompanyDetailsDTO syncCompanyData(String tenantId) throws Exception;
    
    /**
     * Gets all benefits for a given tenant.
     * @param tenantId The unique identifier for the tenant.
     * @throws Exception if the API call fails.
     */
    void getAllBenefits(String tenantId) throws Exception;
} 