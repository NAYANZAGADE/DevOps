package com.glidingpath.finch.batch;

import com.glidingpath.finch.reader.FinchEmployeeReader;
import com.glidingpath.finch.processor.FinchEmployeeProcessor;
import com.glidingpath.finch.writer.FinchEmployeeWriter;
import com.glidingpath.finch.listener.FinchEmployeeStepListener;
import com.glidingpath.finch.constants.FinchConstants;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.finch.exception.FinchException;
import com.glidingpath.common.util.ErrorCode;
import com.tryfinch.api.models.IndividualInDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FinchEmployeeSyncBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job finchEmployeeSyncJob(FinchEmployeeReader reader, 
                                  FinchEmployeeProcessor processor, 
                                  FinchEmployeeWriter writer,
                                  FinchEmployeeStepListener stepListener) {
        return new JobBuilder("finchEmployeeSyncJob", jobRepository)
                .start(finchEmployeeSyncStep(reader, processor, writer, stepListener))
                .build();
    }

    @Bean
    public Step finchEmployeeSyncStep(FinchEmployeeReader reader, 
                                    FinchEmployeeProcessor processor, 
                                    FinchEmployeeWriter writer,
                                    FinchEmployeeStepListener stepListener) {
        return new StepBuilder("finchEmployeeSyncStep", jobRepository)
                .<IndividualInDirectory, PlanParticipant>chunk(FinchConstants.CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(stepListener)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(FinchConstants.BATCH_RETRY_LIMIT)
                .skipLimit(FinchConstants.SKIP_LIMIT)
                .skipPolicy(new FinchSkipPolicy())
                .build();
    }
    
    public static class FinchSkipPolicy implements SkipPolicy {
        
        @Override
        public boolean shouldSkip(Throwable exception, long skipCount) {
            if (exception instanceof FinchException) {
                FinchException finchException = (FinchException) exception;
                ErrorCode errorCode = finchException.getErrorCode();
                
                // Skip data not found errors and mapping errors
                if (errorCode == ErrorCode.FINCH_DATA_NOT_FOUND || 
                    errorCode == ErrorCode.FINCH_MAPPING_ERROR) {
                    log.warn("Skipping FinchException with error code {} (skip count: {}): {}", 
                        errorCode, skipCount, finchException.getMessage());
                    return true;
                }
            }
            
            // Skip database constraint violations and other recoverable errors
            if (exception instanceof org.springframework.dao.DataIntegrityViolationException ||
                exception instanceof org.hibernate.exception.ConstraintViolationException ||
                exception instanceof java.sql.SQLException) {
                log.warn("Skipping database error (skip count: {}): {}", 
                    skipCount, exception.getMessage());
                return true;
            }
            
            return false;
        }
    }
} 