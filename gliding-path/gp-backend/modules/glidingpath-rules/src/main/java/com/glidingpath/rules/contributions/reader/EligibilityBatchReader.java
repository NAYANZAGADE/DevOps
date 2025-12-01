package com.glidingpath.rules.contributions.reader;

import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.stereotype.Component;

import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.repository.PlanParticipantRepository;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EligibilityBatchReader implements ItemStreamReader<PlanParticipant> {

    private final PlanParticipantRepository planParticipantRepository;

    private String tenantId;
    private List<PlanParticipant> employees;
    private int currentIndex = 0;
    private boolean initialized = false;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.tenantId = stepExecution.getJobExecution().getJobParameters()
                            .getString(BatchConstants.JOB_PARAM_TENANT_ID);

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant ID is required for eligibility batch processing");
        }

        log.info("Initializing EligibilityBatchReader for tenant: {}", tenantId);
    }

    @Override
    public void open(ExecutionContext executionContext) {
        // Load all employees for the tenant using safe method that avoids lazy loading issues
        employees = planParticipantRepository.findByTenantIdOrderByCreatedAtDescSafe(tenantId);
        
        // Restore cursor position from previous execution (if restarting)
        currentIndex = executionContext.getInt("cursor", 0);
        
        log.info("Loaded {} employees for tenant {} (starting from index {})", 
                employees.size(), tenantId, currentIndex);
        
        initialized = true;
    }

    @Override
    public PlanParticipant read() {
        if (!initialized) {
            throw new IllegalStateException("Reader not opened. Call open() first.");
        }

        if (currentIndex < employees.size()) {
            PlanParticipant employee = employees.get(currentIndex);
            currentIndex++;
            
            log.debug("Reading employee {} for tenant {} (index {}/{})", 
                    employee.getIndividualId(), tenantId, currentIndex, employees.size());
            
            return employee;
        } else {
            log.info("All {} employees have been read for tenant {}", employees.size(), tenantId);
            return null; // Signal end of data
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        // Save current cursor position for restart capability
        executionContext.putInt("cursor", currentIndex);
        log.debug("Updated cursor position to {} for tenant {}", currentIndex, tenantId);
    }

    @Override
    public void close() {
        log.info("Closing EligibilityBatchReader for tenant {} (processed {}/{} employees)", 
                tenantId, currentIndex, employees != null ? employees.size() : 0);
        initialized = false;
    }
}
