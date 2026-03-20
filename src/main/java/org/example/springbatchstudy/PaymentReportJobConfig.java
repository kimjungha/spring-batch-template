package org.example.springbatchstudy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchstudy.entity.Payment;
import org.example.springbatchstudy.entity.PaymentSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@AllArgsConstructor
public class PaymentReportJobConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

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

    @Bean
    public Step paymentReportStep(PlatformTransactionManager transactionManager){
        return new StepBuilder("paymentReportStep",jobRepository)
                .<PaymentSource, Payment> chunk(10,transactionManager)
                .reader(paymentReportReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<PaymentSource> paymentReportReader(){
        log.info("ItemReader 읽기 수행");
        return new JpaPagingItemReaderBuilder<PaymentSource>()
                .name("paymentReportReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT ps FROM PaymentSource ps")
                .pageSize(10)
                .build();
    }

    private ItemWriter<Payment> itemWriter(){
        return items -> {
            items.forEach(item -> {
                log.info("Payment 로그 출력 : 금액 ={}, 결제일={},상태={}",
                        item.getAmount(),
                        item.getPaymentDate(),
                        item.getStatus()
                );
            });
        };
    }


}
