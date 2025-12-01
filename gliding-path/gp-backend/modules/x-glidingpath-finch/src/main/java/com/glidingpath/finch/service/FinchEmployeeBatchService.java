package com.glidingpath.finch.service;

import com.glidingpath.finch.dto.SyncResponseDTO;
import com.glidingpath.finch.dto.FinchEmployeeDetailsDTO;

public interface FinchEmployeeBatchService {
    
    /**
     * Execute Spring Batch job to sync employees from Finch
     * @param tenantId The tenant ID to sync employees for
     * @return SyncResponseDTO containing sync results and statistics
     */
    SyncResponseDTO<FinchEmployeeDetailsDTO> executeEmployeeSyncJob(String tenantId);
} 