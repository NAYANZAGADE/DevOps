package com.glidingpath.finch.listener;

import com.glidingpath.finch.processor.FinchEmployeeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FinchEmployeeStepListener implements StepExecutionListener {

	@Autowired
	private FinchEmployeeProcessor employeeProcessor;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("Starting step: {} with job execution ID: {}", 
			stepExecution.getStepName(), stepExecution.getJobExecutionId());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("Step completed: {} with status: {}", 
			stepExecution.getStepName(), stepExecution.getStatus());
		
		// Log any failures
		if (stepExecution.getFailureExceptions() != null && !stepExecution.getFailureExceptions().isEmpty()) {
			log.warn("Step completed with {} failure exceptions", stepExecution.getFailureExceptions().size());
		}
		
		// Flush any remaining batch data
		try {
			employeeProcessor.flushBatchData();
		} catch (Exception e) {
			log.error("Failed to flush batch data for step: {}", stepExecution.getStepName(), e);
		}
		
		return stepExecution.getExitStatus();
	}
} 