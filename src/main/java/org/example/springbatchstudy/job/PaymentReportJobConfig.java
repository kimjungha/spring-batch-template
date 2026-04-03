package org.example.springbatchstudy.job;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchstudy.entity.*;
import org.example.springbatchstudy.service.PartnerCorporationService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;

@Slf4j
@Configuration
@AllArgsConstructor
public class PaymentReportJobConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PaymentRepository paymentRepository;
    private final PartnerCorporationService partnerCorporationService;

    private final StepDurationTrackerListener stepDurationTrackerListener;
    private final ChunkDurationTrackerListener chunkDurationTrackerListener;
    private final int chuckSize = 1_000;

    /**
     * JpaPagingItemReader  ->  limit, offset 기반의 sql 조회 만들기
     */

    @Bean
    public Job paymentReportJob(
            Step paymentReportStep
    ){
        return new JobBuilder("paymentReportJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(paymentReportStep)
                .build();
    }

    /*
    *  상호명을  더 이상 paymentSource 에서 관리하지 않겠다
    */
    @Bean
    public Step paymentReportStep(
            JpaPagingItemReader<PaymentSource> paymentReportReader
    ){
        return new StepBuilder("paymentReportStep",jobRepository)
                .<PaymentSource, Payment>chunk(chuckSize,transactionManager)
                .listener(stepDurationTrackerListener)
                .reader(paymentReportReader)
                .processor(itemProcessor())
                .writer(paymentJpaItemWriter())
                .listener(chunkDurationTrackerListener)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<PaymentSource> paymentReportReader(
            @Value("#{jobParameters['paymentDate']}") LocalDate paymentDate
    ){
        log.info("ItemReader 읽기 수행");
        return new JpaPagingItemReaderBuilder<PaymentSource>()
                .name("paymentReportReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT ps FROM PaymentSource ps WHERE ps.paymentDate = :paymentDate ORDER BY ps.id ASC")
                .parameterValues(Collections.singletonMap("paymentDate",paymentDate))
                .pageSize(chuckSize)
                .build();
    }

    private ItemProcessor<PaymentSource, Payment> itemProcessor(){
        return paymentSource -> {

            final String partnerCorpName = partnerCorporationService.getPartnerCorpName(
                    paymentSource.getPartnerBusinessRegistrationNumber());

            return new Payment(
                null,
                    paymentSource.getFinalAmount(),
                    paymentSource.getPaymentDate(),
                    partnerCorpName,
                    "PAYMENT"
            );
        };
    }

    private ItemWriter<Payment> itemWriter(){
        return paymentRepository::saveAll;
    }

    @Bean
    public JpaItemWriter<Payment> paymentJpaItemWriter(){
        JpaItemWriter<Payment> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public ItemWriter<Payment> paymentReportWriter(){
        return chunk -> {
            for(Payment payment:chunk){
                log.info("Writer payment: {}",payment);
            }
        };
    }


}
