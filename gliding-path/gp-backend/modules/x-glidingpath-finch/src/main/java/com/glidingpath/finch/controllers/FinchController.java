package com.glidingpath.finch.controllers;

import com.glidingpath.common.dto.FinchBenefitDTO;
import com.glidingpath.finch.dto.FinchCompanyDetailsDTO;
import com.glidingpath.finch.dto.FinchEmployeeDetailsDTO;
import com.glidingpath.finch.dto.SyncResponseDTO;
import com.glidingpath.common.service.FinchBenefitService;
import com.glidingpath.finch.service.FinchEmployeeBatchService;
import com.glidingpath.finch.service.FinchDataFetcherService;
import com.glidingpath.platform.sponsor.service.OnboardingCompanyStateService;
import com.glidingpath.common.util.OnBoardingState;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/finch")
@Tag(name = "Finch Data Integration", description = "APIs for syncing company and employee data from Finch HRIS platform and managing benefit enrollments")
@RequiredArgsConstructor
@Validated
public class FinchController {

    private final FinchDataFetcherService syncService;
    private final FinchEmployeeBatchService batchEmployeeService;
    private final OnboardingCompanyStateService onboardingService;
    private final FinchBenefitService benefitService;
    
    /**
     * Endpoint to sync company data from Finch HRIS.
     *
     * @return ResponseEntity with FinchCompanyDetailsDTO containing company information
     * @throws Exception if sync operation fails
     */
    @Operation(summary = "Sync company data from Finch", description = "Fetches and stores company information from Finch")
    @GetMapping("/company/sync")
    public ResponseEntity<FinchCompanyDetailsDTO> syncCompany(@CurrentUser User user) throws Exception {
        String tenantId = user.getTenantId();
        log.info("Syncing company data for tenantId={}", tenantId);
        FinchCompanyDetailsDTO company = syncService.syncCompanyData(tenantId);
        onboardingService.updateState(tenantId, OnBoardingState.FINCH);
        return ResponseEntity.ok(company);
    }
    
    /**
     * Endpoint to sync employee data using Spring Batch processing.
     *
     * @return SyncResponseDTO with employee sync results
     */
    @Operation(summary = "Sync employee data using Spring Batch", description = "Fetches employee data from Finch using Spring Batch processing for large datasets")
    @PostMapping("/employees/sync")
    public SyncResponseDTO<FinchEmployeeDetailsDTO> syncEmployees(@CurrentUser User user) {
        String tenantId = user.getTenantId();
        log.info("Starting employee sync for tenantId={}", tenantId);
        SyncResponseDTO<FinchEmployeeDetailsDTO> response = batchEmployeeService.executeEmployeeSyncJob(tenantId);
        // Update state to FINCH after successful employee sync
        if ("SUCCESS".equals(response.getStatus())) {
            onboardingService.updateState(tenantId, OnBoardingState.FINCH);
        }
        var summary = response.getSummary();
        log.info("Employee sync completed for tenantId={}, newRecords={}, existingRecords={}", 
                tenantId, summary != null ? summary.getNewRecords() : 0, summary != null ? summary.getExistingRecords() : 0);
        return response;
    }

 // ========== BENEFIT ENDPOINTS ==========

    /**
     * Endpoint to get all company benefits from Finch.
     *
     * @return ResponseEntity with List of FinchBenefitDTO containing all benefits
     * @throws Exception if benefit retrieval fails
     */
    @Operation(summary = "Get all company benefits from Finch", description = "Retrieves all company-wide benefits and deductions configured in Finch")
    @GetMapping("/benefits")
    public ResponseEntity<List<FinchBenefitDTO>> getAllBenefits(@CurrentUser User user) throws Exception {
        String tenantId = user.getTenantId();
        log.info("Fetching all benefits for tenantId={}", tenantId);
        List<FinchBenefitDTO> benefits = benefitService.getAllBenefits(tenantId);
        return ResponseEntity.ok(benefits);
    }

    /**
     * Endpoint to get specific benefit by ID.
     *
     * @param benefitId the benefit identifier
     * @param tenantId the tenant identifier
     * @return ResponseEntity with FinchBenefitDTO containing benefit details
     * @throws Exception if benefit retrieval fails
     */
    @Operation(summary = "Get specific benefit by ID", description = "Retrieves detailed information for a specific benefit or deduction from Finch")
    @GetMapping("/benefits/{benefitId}")
    public ResponseEntity<FinchBenefitDTO> getBenefitById(
            @CurrentUser User user,
            @PathVariable("benefitId") @NotBlank(message = "Benefit ID is required") String benefitId) throws Exception {
        String tenantId =user.getTenantId();
        log.info("Fetching benefit by ID for tenantId={}, benefitId={}", tenantId, benefitId);
        FinchBenefitDTO benefit = benefitService.getBenefitById(tenantId, benefitId);
        return ResponseEntity.ok(benefit);
    }

    /**
     * Endpoint to get enrolled individuals for a specific benefit.
     *
     * @param benefitId the benefit identifier
     * @param tenantId the tenant identifier
     * @return ResponseEntity with Map containing enrolled individuals information
     * @throws Exception if enrollment retrieval fails
     */
    @Operation(summary = "Get enrolled individuals for benefit", description = "Retrieves list of employees enrolled in a specific benefit or deduction")
    @GetMapping("/benefits/{benefitId}/enrolled")
    public ResponseEntity<Map<String, Object>> getEnrolledIndividuals(
            @CurrentUser User user,
            @PathVariable("benefitId") @NotBlank(message = "Benefit ID is required") String benefitId) throws Exception {
        String tenantId =user.getTenantId();
        log.info("Fetching enrolled individuals for tenantId={}, benefitId={}", tenantId, benefitId);
        Map<String, Object> response = benefitService.getEnrolledIndividuals(tenantId, benefitId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to get individual deduction details.
     *
     * @param benefitId the benefit identifier
     * @param individualIds optional list of individual IDs to filter
     * @return ResponseEntity with List of Maps containing individual deduction details
     * @throws Exception if deduction retrieval fails
     */
    @Operation(summary = "Get individual deduction details", description = "Retrieves detailed deduction and enrollment information for specific employees")
    @GetMapping("/benefits/{benefitId}/individuals")
    public ResponseEntity<List<Map<String, Object>>> getIndividualDeductions(
            @CurrentUser User user,
            @PathVariable("benefitId") @NotBlank(message = "Benefit ID is required") String benefitId,
            @RequestParam(value = "individual_ids", required = false) List<String> individualIds) throws Exception {
        String tenantId = user.getTenantId();
        log.info("Fetching individual deductions for tenantId={}, benefitId={}", tenantId, benefitId);
        List<Map<String, Object>> response = benefitService.getIndividualDeductions(tenantId, benefitId, individualIds);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to register existing deduction with Finch.
     *
     * @param tenantId the tenant identifier
     * @param requestBody Map containing deduction registration details
     * @return ResponseEntity with Map containing registration response
     * @throws Exception if registration fails
     */
    @Operation(summary = "Register existing deduction with Finch", description = "Registers an existing company deduction or contribution with Finch")
    @PostMapping("/benefits/register")
    public ResponseEntity<Map<String, String>> registerDeduction(@CurrentUser User user, @RequestBody Map<String, Object> requestBody) throws Exception {
        String tenantId = user.getTenantId();
        log.info("Registering deduction for tenantId={}", tenantId);
        Map<String, String> response = benefitService.registerDeduction(tenantId, requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint to create new company deduction.
     *
     * @param tenantId the tenant identifier
     * @param requestBody Map containing deduction creation details
     * @return ResponseEntity with Map containing creation response
     * @throws Exception if creation fails
     */
    @Operation(summary = "Create new company deduction", description = "Creates a new company-wide deduction or contribution in Finch ")
    @PostMapping("/benefits")
    public ResponseEntity<Map<String, String>> createDeduction(
            @CurrentUser User user,
            @RequestBody Map<String, Object> requestBody) throws Exception {
        String tenantId = user.getTenantId();
        log.info("Creating deduction for tenantId={}", tenantId);
        Map<String, String> response = benefitService.createDeduction(tenantId, requestBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint to update existing benefit configuration.
     *
     * @param benefitId the benefit identifier
     * @param tenantId the tenant identifier
     * @param requestBody Map containing benefit update details
     * @return ResponseEntity with Map containing update response
     * @throws Exception if update fails
     */
    @Operation(summary = "Update existing benefit configuration", description = "Updates configuration for an existing benefit or deduction in Finch")
    @PutMapping("/benefits/{benefitId}")
    public ResponseEntity<Map<String, String>> updateBenefit(
            @CurrentUser User user,
            @PathVariable("benefitId") @NotBlank(message = "Benefit ID is required") String benefitId,
            @RequestBody Map<String, Object> requestBody) throws Exception {
        String tenantId =user.getTenantId();
        log.info("Updating benefit for tenantId={}, benefitId={}", tenantId, benefitId);
        Map<String, String> response = benefitService.updateBenefit(tenantId, benefitId, requestBody);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to enroll employees in a benefit.
     *
     * @param benefitId the benefit identifier
     * @param tenantId the tenant identifier
     * @param enrollments List of Maps containing enrollment details
     * @return ResponseEntity with Map containing enrollment response
     * @throws Exception if enrollment fails
     */
    @Operation(summary = "Enroll employees in benefit", description = "Enrolls specific employees in a deduction or contribution benefit")
    @PostMapping("/benefits/{benefitId}/individuals")
    public ResponseEntity<Map<String, Object>> enrollIndividualsInDeduction(
            @CurrentUser User user,
            @PathVariable("benefitId") @NotBlank(message = "Benefit ID is required") String benefitId,
            @RequestBody List<Map<String, Object>> enrollments) throws Exception {
        String tenantId = user.getTenantId();
        log.info("Enrolling individuals in deduction for tenantId={}, benefitId={}", tenantId, benefitId);
        Map<String, Object> response = benefitService.enrollIndividualsInDeduction(tenantId, benefitId, enrollments);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to unenroll employees from a benefit.
     *
     * @param benefitId the benefit identifier
     * @param tenantId the tenant identifier
     * @param requestBody Map containing individual IDs to unenroll
     * @return ResponseEntity with Map containing unenrollment response
     * @throws Exception if unenrollment fails
     */
    @Operation(summary = "Unenroll employees from benefit", description = "Removes employees from a deduction or contribution benefit enrollment")
    @DeleteMapping("/benefits/{benefitId}/individuals")
    public ResponseEntity<Map<String, Object>> unenrollIndividualsFromDeduction(
            @CurrentUser User user,
            @PathVariable("benefitId") @NotBlank(message = "Benefit ID is required") String benefitId,
            @RequestBody Map<String, List<String>> requestBody) throws Exception {
        String tenantId = user.getTenantId();
        log.info("Unenrolling individuals from deduction for tenantId={}, benefitId={}",tenantId, benefitId);
        List<String> individualIds = requestBody.get("individual_ids");
        Map<String, Object> response = benefitService.unenrollIndividualsFromDeduction(tenantId, benefitId, individualIds);
        return ResponseEntity.ok(response);
    }
}