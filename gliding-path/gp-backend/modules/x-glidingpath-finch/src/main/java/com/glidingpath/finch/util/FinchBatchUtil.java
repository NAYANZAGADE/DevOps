package com.glidingpath.finch.util;

import com.glidingpath.finch.constants.FinchConstants;
import com.glidingpath.finch.exception.FinchException;
import com.glidingpath.finch.reader.FinchEmployeeReader;
import com.glidingpath.finch.processor.FinchEmployeeProcessor;
import com.glidingpath.finch.writer.FinchEmployeeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

@Slf4j
public final class FinchBatchUtil {
	
	private FinchBatchUtil() {
		// Prevent instantiation
	}

	public static void initializeBatchComponents(
			FinchEmployeeReader reader,
			FinchEmployeeProcessor processor,
			FinchEmployeeWriter writer,
			String tenantId) { // Initializes all Spring Batch components with tenant context
		try {
			reader.initialize(tenantId); // Initializes reader with tenant-specific Finch client
			processor.initialize(tenantId); // Initializes processor with tenant context
			processor.resetForNewJob(); // Clears processor caches for new job execution
			writer.initialize(tenantId); // Initializes writer with tenant context and resets statistics
			log.debug("Successfully initialized all batch components for tenantId: {}", tenantId);
		} catch (Exception e) {
			log.error("Failed to initialize batch components for tenantId: {}", tenantId, e);
			throw FinchException.initializationError(FinchConstants.BATCH_INIT_ERROR + e.getMessage());
		}
	}
	
	public static JobParameters createJobParameters(String tenantId) {
		return new JobParametersBuilder()
				.addString(FinchConstants.JOB_PARAM_TENANT_ID, tenantId)
				.addLong(FinchConstants.JOB_PARAM_TIMESTAMP, System.currentTimeMillis())
				.toJobParameters();
	}
	
	public static String formatSuccessMessage(
			int totalProcessed, 
			int newRecords, 
			int existingRecords, 
			int failedRecords) {
		return String.format(FinchConstants.SUCCESS_MESSAGE_TEMPLATE,
				totalProcessed, newRecords, existingRecords, failedRecords);
	}
	
	public static String formatErrorMessage(String tenantId, String status) {
		return String.format(FinchConstants.ERROR_MESSAGE_TEMPLATE, tenantId, status);
	}
} 