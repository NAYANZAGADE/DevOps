package com.glidingpath.common.service;

import java.util.List;
import java.util.Map;

import com.glidingpath.common.dto.FinchBenefitDTO;

/**
 * Service interface for managing Finch benefits, deductions, and enrollments.
 */
public interface FinchBenefitService {
    
    /**
     * Fetches all company-wide benefits/deductions from Finch for the given tenant.
     * @param tenantId The unique identifier for the tenant.
     * @return List of FinchBenefitDTO.
     * @throws Exception if the API call fails.
     */
    List<FinchBenefitDTO> getAllBenefits(String tenantId) throws Exception;

    /**
     * Fetches a specific benefit by ID.
     * @param tenantId The unique identifier for the tenant.
     * @param benefitId The benefit ID.
     * @return FinchBenefitDTO for the specified benefit.
     * @throws Exception if the API call fails.
     */
    FinchBenefitDTO getBenefitById(String tenantId, String benefitId) throws Exception;

    /**
     * Registers a new deduction with Finch.
     * @param tenantId The unique identifier for the tenant.
     * @param requestBody The deduction registration request body.
     * @return Map containing the response from Finch.
     * @throws Exception if the API call fails.
     */
    Map<String, String> registerDeduction(String tenantId, Map<String, Object> requestBody) throws Exception;

    /**
     * Creates a new deduction with Finch.
     * @param tenantId The unique identifier for the tenant.
     * @param requestBody The deduction creation request body.
     * @return Map containing the response from Finch.
     * @throws Exception if the API call fails.
     */
    Map<String, String> createDeduction(String tenantId, Map<String, Object> requestBody) throws Exception;
    
    /**
     * Updates an existing benefit.
     * @param tenantId The unique identifier for the tenant.
     * @param benefitId The benefit ID to update.
     * @param requestBody The update request body.
     * @return Map containing the response from Finch.
     * @throws Exception if the API call fails.
     */
    Map<String, String> updateBenefit(String tenantId, String benefitId, Map<String, Object> requestBody) throws Exception;

    /**
     * Gets enrolled individuals for a specific benefit.
     * @param tenantId The unique identifier for the tenant.
     * @param benefitId The benefit ID.
     * @return Map containing enrollment information.
     * @throws Exception if the API call fails.
     */
    Map<String, Object> getEnrolledIndividuals(String tenantId, String benefitId) throws Exception;

    /**
     * Fetches deduction/enrollment information for the given individuals for a specific benefit.
     * @param tenantId The unique identifier for the tenant.
     * @param benefitId The benefit ID.
     * @param individualIds List of individual IDs (can be null or empty for all individuals).
     * @return List of maps representing deduction/enrollment info for each individual.
     * @throws Exception if the API call fails.
     */
    List<Map<String, Object>> getIndividualDeductions(String tenantId, String benefitId, List<String> individualIds) throws Exception;

    /**
     * Enroll individuals in a deduction for a benefit.
     * @param tenantId The tenant ID.
     * @param benefitId The benefit ID.
     * @param enrollments List of enrollment objects.
     * @return Map with job_id or response from Finch.
     * @throws Exception if the API call fails.
     */
    Map<String, Object> enrollIndividualsInDeduction(String tenantId, String benefitId, List<Map<String, Object>> enrollments) throws Exception;

    /**
     * Unenroll individuals from a deduction for a benefit.
     * @param tenantId The tenant ID.
     * @param benefitId The benefit ID.
     * @param individualIds List of individual IDs to unenroll.
     * @return Map with job_id or response from Finch.
     * @throws Exception if the API call fails.
     */
    Map<String, Object> unenrollIndividualsFromDeduction(String tenantId, String benefitId, List<String> individualIds) throws Exception;
}
