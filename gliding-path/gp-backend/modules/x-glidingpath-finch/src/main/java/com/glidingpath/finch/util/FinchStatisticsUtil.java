package com.glidingpath.finch.util;

import com.glidingpath.finch.dto.SyncResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

@Slf4j
public final class FinchStatisticsUtil {
	
	private FinchStatisticsUtil() {
		// Prevent instantiation
	}
	
	public static SyncResponseDTO.SyncSummary extractJobStatistics(JobExecution jobExecution, String tenantId) {
		long totalProcessed = 0;
		long newRecords = 0;
		long existingRecords = 0;
		long failedRecords = 0;
		long skippedRecords = 0;

		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			Statistics stats = extractStepStatistics(stepExecution);
			
			totalProcessed += stats.totalProcessed;
			newRecords += stats.newRecords;
			existingRecords += stats.existingRecords;
			failedRecords += stats.failedRecords;
			skippedRecords += stats.skippedRecords;
		}

		log.info("Job statistics for tenantId: {} - Total: {}, New: {}, Existing: {}, Failed: {}, Skipped: {}", 
			tenantId, totalProcessed, newRecords, existingRecords, failedRecords, skippedRecords);

		return SyncResponseDTO.SyncSummary.builder()
				.totalProcessed((int) totalProcessed)
				.newRecords((int) newRecords)
				.existingRecords((int) existingRecords)
				.failedRecords((int) (failedRecords + skippedRecords))
				.tenantId(tenantId)
				.build();
	}
	
	private static Statistics extractStepStatistics(StepExecution stepExecution) {
		// Try to get custom statistics from the writer first
		Number customTotalProcessed = (Number) stepExecution.getExecutionContext().get("custom.totalProcessed");
		Number customNewRecords = (Number) stepExecution.getExecutionContext().get("custom.newRecords");
		Number customExistingRecords = (Number) stepExecution.getExecutionContext().get("custom.existingRecords");

		if (customTotalProcessed != null && customNewRecords != null && customExistingRecords != null) {
			// Use custom statistics from the writer
			long totalProcessed = customTotalProcessed.longValue();
			long newRecords = customNewRecords.longValue();
			long existingRecords = customExistingRecords.longValue();
			long skippedRecords = stepExecution.getSkipCount();
			long failedRecords = stepExecution.getRollbackCount();

			return new Statistics(totalProcessed, newRecords, existingRecords, failedRecords, skippedRecords);
		} else {
			// Fallback to Spring Batch counters (for backward compatibility)
			long totalProcessed = stepExecution.getReadCount();
			long newRecords = stepExecution.getWriteCount();
			long skippedRecords = stepExecution.getSkipCount();
			long failedRecords = stepExecution.getRollbackCount();

			// Calculate existing records (processed but not new)
			long existingRecords = Math.max(0, totalProcessed - newRecords - skippedRecords);

			return new Statistics(totalProcessed, newRecords, existingRecords, failedRecords, skippedRecords);
		}
	}
	
	private static class Statistics {
		final long totalProcessed;
		final long newRecords;
		final long existingRecords;
		final long failedRecords;
		final long skippedRecords;

		Statistics(long totalProcessed, long newRecords, long existingRecords, long failedRecords, long skippedRecords) {
			this.totalProcessed = totalProcessed;
			this.newRecords = newRecords;
			this.existingRecords = existingRecords;
			this.failedRecords = failedRecords;
			this.skippedRecords = skippedRecords;
		}
	}
} 