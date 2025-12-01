package com.glidingpath.finch.writer;

import com.glidingpath.core.entity.PlanSponsor;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.entity.IncomeHistory;
import com.glidingpath.core.repository.PlanParticipantRepository;
import com.glidingpath.core.repository.PlanSponsorRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FinchEmployeeWriter implements ItemWriter<PlanParticipant> {

    @Autowired
    private PlanParticipantRepository repository;

    @Autowired
    private PlanSponsorRepository companyRepository;

    private String tenantId;
    private StepExecution stepExecution;
    
    // Statistics tracking
    private int totalProcessed = 0;
    private int newRecords = 0;
    private int existingRecords = 0;

    public void initialize(String tenantId) throws Exception {
        this.tenantId = tenantId;
        this.totalProcessed = 0;
        this.newRecords = 0;
        this.existingRecords = 0;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends PlanParticipant> chunk) throws Exception {
        List<? extends PlanParticipant> employees = chunk.getItems();
        if (employees == null || employees.isEmpty()) {
            return;
        }

        // Get company for relationship
        PlanSponsor company = getCompanyForTenant();
        
        // Extract all individual IDs for batch existence check
        Set<String> individualIds = extractIndividualIds(employees);
        Set<String> existingIndividualIds = getExistingIndividualIds(individualIds);

        // Process employees and separate new from existing
        List<PlanParticipant> newEmployees = processEmployees(employees, company, existingIndividualIds);

        // Save new employees
        saveNewEmployees(newEmployees);
    }

    private PlanSponsor getCompanyForTenant() {
        return companyRepository.findByTenantId(tenantId).orElse(null);
    }

    private Set<String> extractIndividualIds(List<? extends PlanParticipant> employees) {
        return employees.stream()
            .filter(employee -> employee != null && employee.getIndividualId() != null)
            .map(PlanParticipant::getIndividualId)
            .collect(Collectors.toSet());
    }

    private Set<String> getExistingIndividualIds(Set<String> individualIds) {
        if (individualIds.isEmpty()) {
            return Set.of();
        }
        
        return repository.findByIndividualIdIn(individualIds)
            .stream()
            .map(PlanParticipant::getIndividualId)
            .collect(Collectors.toSet());
    }

    private List<PlanParticipant> processEmployees(
            List<? extends PlanParticipant> employees, 
            PlanSponsor company, 
            Set<String> existingIndividualIds) {
        
        List<PlanParticipant> newEmployees = new java.util.ArrayList<>();

        for (PlanParticipant employee : employees) {
            if (employee == null) {
                throw new RuntimeException("Null employee received in writer");
            }

            try {
                // Set company relationship if available
                if (company != null) {
                    employee.setCompany(company);
                }

                // Set employeeId for income history records
                if (employee.getIncomeHistory() != null) {
                    for (IncomeHistory history : employee.getIncomeHistory()) {
                        history.setEmployeeId(employee.getId());
                    }
                }
                
                // Check if employee already exists using batch result
                if (!existingIndividualIds.contains(employee.getIndividualId())) {
                    newEmployees.add(employee);
                    newRecords++;
                } else {
                    existingRecords++;
                }
                
                totalProcessed++;

            } catch (Exception e) {
                log.error("Error processing employee: {}", employee.getIndividualId(), e);
                throw e;
            }
        }

        return newEmployees;
    }

    private void saveNewEmployees(List<PlanParticipant> newEmployees) {
        if (!newEmployees.isEmpty()) {
            try {
                repository.saveAll(newEmployees);
            } catch (Exception e) {
                log.error("Failed to save employees for tenantId: {}", tenantId, e);
                throw e;
            }
        }
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        // Store custom statistics in the step execution context
        stepExecution.getExecutionContext().put("custom.totalProcessed", totalProcessed);
        stepExecution.getExecutionContext().put("custom.newRecords", newRecords);
        stepExecution.getExecutionContext().put("custom.existingRecords", existingRecords);
    }
} 