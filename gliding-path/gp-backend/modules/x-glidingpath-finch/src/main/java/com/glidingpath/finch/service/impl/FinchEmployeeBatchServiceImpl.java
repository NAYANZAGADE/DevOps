package com.glidingpath.finch.service.impl;

import com.glidingpath.finch.reader.FinchEmployeeReader;
import com.glidingpath.finch.processor.FinchEmployeeProcessor;
import com.glidingpath.finch.writer.FinchEmployeeWriter;
import com.glidingpath.finch.dto.SyncResponseDTO;
import com.glidingpath.finch.dto.FinchEmployeeDetailsDTO;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.repository.PlanParticipantRepository;
import com.glidingpath.finch.exception.FinchException;
import com.glidingpath.finch.mapping.FinchMapping;
import com.glidingpath.finch.service.FinchEmployeeBatchService;
import com.glidingpath.finch.util.FinchBatchUtil;
import com.glidingpath.finch.util.FinchStatisticsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinchEmployeeBatchServiceImpl implements FinchEmployeeBatchService {

    private final JobLauncher jobLauncher;
    private final FinchEmployeeReader employeeReader;
    private final FinchEmployeeProcessor employeeProcessor;
    private final FinchEmployeeWriter employeeWriter;
    private final PlanParticipantRepository employeeRepository;
    
    @Autowired
    private Job finchEmployeeSyncJob;

    @Override
    public SyncResponseDTO<FinchEmployeeDetailsDTO> executeEmployeeSyncJob(String tenantId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Initialize batch components
            FinchBatchUtil.initializeBatchComponents(employeeReader, employeeProcessor, employeeWriter, tenantId);

            // Create job parameters and execute job
            JobParameters jobParameters = FinchBatchUtil.createJobParameters(tenantId);
            JobExecution jobExecution = jobLauncher.run(finchEmployeeSyncJob, jobParameters);
            
            // Extract statistics and create response
            SyncResponseDTO.SyncSummary summary = FinchStatisticsUtil.extractJobStatistics(jobExecution, tenantId); 
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Batch job completed for tenantId: {}, duration: {}ms, status: {}", 
                tenantId, duration, jobExecution.getStatus());

            return createResponse(jobExecution, summary, tenantId);

        } catch (FinchException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to execute batch job for tenantId: {}", tenantId, e);
            throw FinchException.batchError("Failed to execute batch job: " + e.getMessage());
        }
    }

    private SyncResponseDTO<FinchEmployeeDetailsDTO> createResponse(JobExecution jobExecution, SyncResponseDTO.SyncSummary summary, String tenantId) { // Creates response based on job execution status
        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            String errorMessage = FinchBatchUtil.formatErrorMessage(summary.getTenantId(), jobExecution.getStatus().toString());
            log.error(errorMessage);
            return SyncResponseDTO.error(errorMessage, summary);
        }

        String message = FinchBatchUtil.formatSuccessMessage(
            summary.getTotalProcessed(), 
            summary.getNewRecords(), 
            summary.getExistingRecords(), 
            summary.getFailedRecords()
        );
        
        // Return response based on whether there are new records
        return summary.getNewRecords() > 0 
            ? createResponseWithData(message, summary, tenantId)
            : SyncResponseDTO.noNewRecords(message, summary);
    }
    
    private SyncResponseDTO<FinchEmployeeDetailsDTO> createResponseWithData(String message, SyncResponseDTO.SyncSummary summary, String tenantId) {
        // Fetch only the required number of recent employees directly from database
        List<PlanParticipant> recentEmployees = employeeRepository.findRecentByTenantIdOrderByCreatedAtDesc(tenantId, summary.getNewRecords());
        
        if (recentEmployees.isEmpty()) {
            log.warn("No recent employees found for tenantId: {}, returning response without data", tenantId);
            return SyncResponseDTO.noNewRecords(message, summary);
        }
        
        // Convert entities to DTOs in a single stream operation
        List<FinchEmployeeDetailsDTO> employeeDTOs = recentEmployees.stream()
            .map(FinchMapping::toDTO)
            .collect(Collectors.toList());
        
        return SyncResponseDTO.success(message, employeeDTOs, summary);
    }
} 