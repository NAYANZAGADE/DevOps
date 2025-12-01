package com.glidingpath.rules.contributions;

import constants.BatchConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.glidingpath.rules.contributions.listener.PrePayrollBatchListener;
import com.glidingpath.rules.contributions.listener.StepListener;
import com.glidingpath.rules.contributions.processor.CalculationBatchProcessor;
import com.glidingpath.rules.contributions.processor.EligibilityBatchProcessor;
import com.glidingpath.rules.contributions.processor.FinchDeductionBatchProcessor;
import com.glidingpath.rules.contributions.reader.CalculationBatchReader;
import com.glidingpath.rules.contributions.reader.EligibilityBatchReader;
import com.glidingpath.rules.contributions.reader.FinchDeductionBatchReader;
import com.glidingpath.rules.contributions.writer.CalculationBatchWriter;
import com.glidingpath.rules.contributions.writer.EligibilityBatchWriter;
import com.glidingpath.rules.contributions.writer.FinchDeductionBatchWriter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PrePayrollBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // ========================================
    // COMPLETE PRE-PAYROLL BATCH JOB
    // ========================================
    
    @Bean
    public Job prePayrollJob(PrePayrollBatchListener jobListener,
                            StepListener stepListener,
                            EligibilityBatchReader eligibilityReader,
                            EligibilityBatchProcessor eligibilityProcessor,
                            EligibilityBatchWriter eligibilityWriter,
                            CalculationBatchReader calculationReader,
                            CalculationBatchProcessor calculationProcessor,
                            CalculationBatchWriter calculationWriter,
                            FinchDeductionBatchReader deductionReader,
                            FinchDeductionBatchProcessor deductionProcessor,
                            FinchDeductionBatchWriter deductionWriter) {
        return new JobBuilder("prePayrollJob", jobRepository)
            // COMPLETE 3-STEP BATCH JOB: eligibility -> calculation -> deductions
            // This uses the properly implemented batch components that leverage EligibilityRuleEngineService
            .start(eligibilityStep(eligibilityReader, eligibilityProcessor, eligibilityWriter, stepListener))
            .next(calculationStep(calculationReader, calculationProcessor, calculationWriter, stepListener))
            .next(deductionStep(deductionReader, deductionProcessor, deductionWriter, stepListener))
            .listener(jobListener)
            .build();
    }

    // ========================================
    // STEP 1: ELIGIBILITY EVALUATION
    // ========================================
    // Uses EligibilityBatchReader -> EligibilityBatchProcessor -> EligibilityBatchWriter
    // The processor uses EligibilityProcessingUtility (shared logic, no duplication)
    // The writer updates PlanParticipant entities with eligibility results
    
    @Bean
    public Step eligibilityStep(EligibilityBatchReader reader, 
                               EligibilityBatchProcessor processor, 
                               EligibilityBatchWriter writer,
                               StepListener stepListener) {
        return new StepBuilder("eligibilityStep", jobRepository)
            .<com.glidingpath.core.entity.PlanParticipant, 
               com.glidingpath.common.dto.EmployeeEligibilityDTO>chunk(BatchConstants.ELIGIBILITY_CHUNK_SIZE, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(stepListener)
            .faultTolerant()
            .retry(Exception.class)
            .retryLimit(BatchConstants.ELIGIBILITY_RETRY_LIMIT)
            .skipLimit(BatchConstants.ELIGIBILITY_SKIP_LIMIT)
            .skipPolicy(new EligibilitySkipPolicy())
            .build();
    }

    // ========================================
    // STEP 2: PRE-PAYROLL CALCULATION
    // ========================================
    // Processes eligible employees from Step 1 for pre-payroll calculations
    
    @Bean
    public Step calculationStep(CalculationBatchReader reader, 
                               CalculationBatchProcessor processor, 
                               CalculationBatchWriter writer,
                               StepListener stepListener) {
        return new StepBuilder("calculationStep", jobRepository)
            .<com.glidingpath.common.dto.EmployeeEligibilityDTO, 
               com.glidingpath.common.dto.PrePayrollCalculationDTO>chunk(BatchConstants.CALCULATION_CHUNK_SIZE, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(stepListener)
            .faultTolerant()
            .retry(Exception.class)
            .retryLimit(BatchConstants.CALCULATION_RETRY_LIMIT)
            .skipLimit(BatchConstants.CALCULATION_SKIP_LIMIT)
            .skipPolicy(new CalculationSkipPolicy())
            .build();
    }

    // ========================================
    // STEP 3: FINCH DEDUCTION CREATION
    // ========================================
    // Creates Finch deductions for successful calculations from Step 2
    
    @Bean
    public Step deductionStep(FinchDeductionBatchReader reader, 
                             FinchDeductionBatchProcessor processor, 
                             FinchDeductionBatchWriter writer,
                             StepListener stepListener) {
        return new StepBuilder("deductionStep", jobRepository)
            .<com.glidingpath.common.dto.PrePayrollCalculationDTO, 
               com.glidingpath.common.dto.PrePayrollCalculationDTO>chunk(BatchConstants.DEDUCTION_CHUNK_SIZE, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(stepListener)
            .faultTolerant()
            .retry(Exception.class)
            .retryLimit(BatchConstants.DEDUCTION_RETRY_LIMIT)
            .skipLimit(BatchConstants.DEDUCTION_SKIP_LIMIT)
            .skipPolicy(new FinchDeductionSkipPolicy())
            .build();
    }

    // ========================================
    // SKIP POLICIES
    // ========================================
    
    public static class EligibilitySkipPolicy implements SkipPolicy {
        @Override
        public boolean shouldSkip(Throwable exception, long skipCount) {
            // Skip eligibility processing errors after 3 attempts
            return skipCount < 3 && (exception instanceof RuntimeException || exception instanceof Exception);
        }
    }
    
    public static class CalculationSkipPolicy implements SkipPolicy {
        @Override
        public boolean shouldSkip(Throwable exception, long skipCount) {
            // Skip calculation processing errors after 2 attempts
            return skipCount < 2 && (exception instanceof RuntimeException || exception instanceof Exception);
        }
    }
    
    public static class FinchDeductionSkipPolicy implements SkipPolicy {
        @Override
        public boolean shouldSkip(Throwable exception, long skipCount) {
            // Skip Finch deduction errors after 1 attempt (critical step)
            return skipCount < 1 && (exception instanceof RuntimeException || exception instanceof Exception);
        }
    }
}
