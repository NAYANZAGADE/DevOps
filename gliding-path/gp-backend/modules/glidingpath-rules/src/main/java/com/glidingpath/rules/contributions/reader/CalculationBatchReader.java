package com.glidingpath.rules.contributions.reader;

import com.glidingpath.common.dto.EmployeeEligibilityDTO;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.repository.PlanParticipantRepository;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalculationBatchReader implements ItemReader<EmployeeEligibilityDTO> {

    private final PlanParticipantRepository planParticipantRepository;
    
    private String tenantId;
    private LocalDate payrollPeriodStart;
    private LocalDate payrollPeriodEnd;
    private ListItemReader<EmployeeEligibilityDTO> delegateReader;
    private boolean initialized = false;
    private StepExecution stepExecution;
    
    // Thread-safe counters for monitoring
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.tenantId = stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_TENANT_ID);
        this.payrollPeriodStart = LocalDate.parse(
            stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_PAYROLL_PERIOD_START)
        );
        this.payrollPeriodEnd = LocalDate.parse(
            stepExecution.getJobExecution().getJobParameters().getString(BatchConstants.JOB_PARAM_PAYROLL_PERIOD_END)
        );
        
        log.info("Initializing CalculationBatchReader for tenant: {} period: {} to {}", 
                tenantId, payrollPeriodStart, payrollPeriodEnd);
        
        if (tenantId == null || payrollPeriodStart == null || payrollPeriodEnd == null) {
            throw new IllegalStateException("Tenant ID and payroll period dates are required for calculation batch processing");
        }
        
        // Reset counters for new step execution
        processedCount.set(0);
        totalCount.set(0);
        
        initializeReader();
    }

    private void initializeReader() {
        try {
            // Get eligible employees from job execution context (from previous eligibility step)
            List<EmployeeEligibilityDTO> eligibleEmployees = getEligibleEmployeesFromContext();
            
            if (eligibleEmployees == null || eligibleEmployees.isEmpty()) {
                // Fallback: fetch eligible employees from database
                log.warn("No eligible employees found in job context, fetching from database for tenant: {}", tenantId);
                eligibleEmployees = fetchEligibleEmployeesFromDatabase();
            }
            
            if (eligibleEmployees.isEmpty()) {
                log.warn("No eligible employees found for calculation in tenant: {} - calculation batch will process 0 records", tenantId);
                this.delegateReader = new ListItemReader<>(List.of());
                this.totalCount.set(0);
            } else {
                log.info("Found {} eligible employees for calculation in tenant: {} period: {} to {}", 
                        eligibleEmployees.size(), tenantId, payrollPeriodStart, payrollPeriodEnd);
                
                // Process ALL eligible employees in batches
                this.delegateReader = new ListItemReader<>(eligibleEmployees);
                this.totalCount.set(eligibleEmployees.size());
                log.info("ListItemReader initialized with {} eligible employees for batch processing", eligibleEmployees.size());
            }
            
            this.initialized = true;
            
        } catch (Exception e) {
            log.error("Failed to initialize calculation batch reader for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to initialize calculation batch reader", e);
        }
    }

    /**
     * Get eligible employees from job execution context (from previous eligibility step)
     * This is the PRIMARY source - we don't re-check eligibility here!
     */
    @SuppressWarnings("unchecked")
    private List<EmployeeEligibilityDTO> getEligibleEmployeesFromContext() {
        try {
            // Get eligible employee IDs from job execution context
            Object contextValue = stepExecution.getJobExecution().getExecutionContext()
                .get("eligibleEmployeeIds");
            
            if (contextValue instanceof List) {
                List<String> eligibleEmployeeIds = (List<String>) contextValue;
                
                if (!eligibleEmployeeIds.isEmpty()) {
                    // Convert employee IDs to EmployeeEligibilityDTO objects
                    List<EmployeeEligibilityDTO> eligibleEmployees = eligibleEmployeeIds.stream()
                        .map(this::convertEmployeeIdToEligibilityDto)
                        .filter(dto -> dto != null)
                        .collect(Collectors.toList());
                    
                    log.info("Retrieved {} eligible employees from job execution context (from eligibility step)", eligibleEmployees.size());
                    return eligibleEmployees;
                } else {
                    log.warn("No eligible employee IDs found in job execution context");
                }
            } else {
                log.warn("Job execution context does not contain eligible employee IDs");
            }
            
        } catch (Exception e) {
            log.warn("Failed to retrieve eligible employees from job context: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Fallback: fetch eligible employees from database
     * This should only be used if the job context is empty
     */
    private List<EmployeeEligibilityDTO> fetchEligibleEmployeesFromDatabase() {
        try {
            log.warn("FALLBACK: Fetching eligible employees from database - this indicates a data flow issue");
            
            // Get all employees for the tenant
            List<PlanParticipant> allEmployees = planParticipantRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
            
            // Filter only eligible employees based on stored eligibility status
            List<EmployeeEligibilityDTO> eligibleEmployees = allEmployees.stream()
                .filter(employee -> Boolean.TRUE.equals(employee.getIsEligibleFor401k()))
                .map(this::convertToEligibilityDto)
                .collect(Collectors.toList());
            
            log.info("Fetched {} eligible employees from database for tenant: {}", eligibleEmployees.size(), tenantId);
            return eligibleEmployees;
            
        } catch (Exception e) {
            log.error("Failed to fetch eligible employees from database for tenant: {}", tenantId, e);
            return List.of();
        }
    }

    /**
     * Convert employee ID to EmployeeEligibilityDTO by fetching from database
     */
    private EmployeeEligibilityDTO convertEmployeeIdToEligibilityDto(String employeeId) {
        try {
            Optional<PlanParticipant> employeeOpt = planParticipantRepository.findByIndividualId(employeeId);
            if (employeeOpt.isPresent()) {
                return convertToEligibilityDto(employeeOpt.get());
            } else {
                log.warn("Employee not found for ID: {}", employeeId);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to convert employee ID {} to eligibility DTO", employeeId, e);
            return null;
        }
    }

    /**
     * Convert PlanParticipant to EmployeeEligibilityDTO
     */
    private EmployeeEligibilityDTO convertToEligibilityDto(PlanParticipant employee) {
        EmployeeEligibilityDTO dto = new EmployeeEligibilityDTO();
        dto.setEmployeeId(employee.getIndividualId());
        dto.setTenantId(employee.getTenantId());
        dto.setEligible(true); // We know they're eligible since we filtered for this
        dto.setEligibilityDate(employee.getEligibilityDate());
        dto.setEligibilityReason(employee.getEligibilityReason());
        dto.setCurrentDate(LocalDate.now());
        
        // Set other required fields
        dto.setDateOfBirth(employee.getDob());
        dto.setHireDate(employee.getStartDate());
        dto.setRehireDate(employee.getLatestRehireDate());
        dto.setEmploymentStatus(employee.getEmploymentStatus());
        
        dto.setEmploymentType(employee.getEmploymentType());
        
        // Calculate age and service duration
        dto.age = calculateAge(employee.getDob());
        dto.monthsOfService = calculateMonthsOfService(employee.getStartDate(), employee.getLatestRehireDate());
        
        return dto;
    }

    /**
     * Calculate employee age
     */
    private int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null || LocalDate.now() == null) {
            return 0;
        }
        return java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Calculate months of service
     */
    private int calculateMonthsOfService(LocalDate hireDate, LocalDate rehireDate) {
        LocalDate startDate = rehireDate != null ? rehireDate : hireDate;
        if (startDate == null || LocalDate.now() == null) {
            return 0;
        }
        java.time.Period period = java.time.Period.between(startDate, LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }

    @Override
    public EmployeeEligibilityDTO read() throws Exception {
        try {
            if (!initialized) {
                log.error("CalculationBatchReader not initialized. Call beforeStep() first.");
                throw new IllegalStateException("CalculationBatchReader not initialized. Call beforeStep() first.");
            }
            
            if (delegateReader == null) {
                log.debug("DelegateReader is null, returning null");
                return null;
            }
            
            EmployeeEligibilityDTO eligibleEmployee = delegateReader.read();
            
            if (eligibleEmployee != null) {
                processedCount.incrementAndGet();
                log.debug("Reading eligible employee: {} for calculation ({}/{} processed)", 
                         eligibleEmployee.getEmployeeId(), processedCount.get(), totalCount.get());
                
                // Force Spring Batch to recognize this as a read operation
                // This helps with proper counting in the monitoring
                if (processedCount.get() % BatchConstants.CALCULATION_CHUNK_SIZE == 0) {
                    log.info("Completed calculation chunk: {}/{} eligible employees processed", processedCount.get(), totalCount.get());
                }
                
                // Ensure Spring Batch recognizes this read operation
                return eligibleEmployee;
            } else {
                log.info("No more eligible employees to read for calculation. Total processed: {}/{}", processedCount.get(), totalCount.get());
                return null;
            }
            
        } catch (Exception e) {
            log.error("Error reading eligible employee data for calculation in tenant: {}", tenantId, e);
            log.error("Exception details: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    /**
     * Get the total count of eligible employees to be processed
     */
    public int getTotalEligibleEmployeeCount() {
        if (delegateReader == null) {
            return 0;
        }
        
        try {
            // This is a simple way to get the count, but in production you might want to store it separately
            List<PlanParticipant> allEmployees = planParticipantRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
            return (int) allEmployees.stream()
                .filter(employee -> Boolean.TRUE.equals(employee.getIsEligibleFor401k()))
                .count();
        } catch (Exception e) {
            log.warn("Failed to get total eligible employee count for tenant: {}", tenantId, e);
            return 0;
        }
    }

    /**
     * Check if the reader has been properly initialized
     */
    public boolean isInitialized() {
        return initialized && tenantId != null && payrollPeriodStart != null && payrollPeriodEnd != null && delegateReader != null;
    }

    /**
     * Get the current tenant ID being processed
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Get the payroll period start date
     */
    public LocalDate getPayrollPeriodStart() {
        return payrollPeriodStart;
    }

    /**
     * Get the payroll period end date
     */
    public LocalDate getPayrollPeriodEnd() {
        return payrollPeriodEnd;
    }
}
