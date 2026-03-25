package org.example.springbatchstudy.job;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchstudy.entity.*;
import org.example.springbatchstudy.service.PartnerCorporationService;
import org.example.springbatchstudy.service.PartnerHttpException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
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
                .<PaymentSource, Payment> chunk(10,transactionManager)
                .reader(paymentReportReader)
                .processor(itemProcessor())
                .writer(paymentReportWriter())
                .faultTolerant() // 내결함성 활성화
                .retryLimit(10)
                .retry(PartnerHttpException.class)
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
                .queryString("SELECT ps FROM PaymentSource ps WHERE ps.paymentDate = :paymentDate")
                .parameterValues(Collections.singletonMap("paymentDate",paymentDate))
                .pageSize(10)
                .build();
    }

    private ItemProcessor<PaymentSource, Payment> itemProcessor(){
        /**
        * 최종 결제 금액이 0 원인경우는 payment 에 저장되지 않는다.
        */
        return paymentSource -> {
//            최종 금액 0 원인 경우 제외
//            if(paymentSource.getFinalAmount().compareTo(BigDecimal.ZERO) ==0){
//                return null;
//            }

            // 할인금액이 음수되는 경우
//            if(paymentSource.getDiscountAmount().signum() == -1){
//                final String msg = "할인 금액이 0 이 아닌 결제는 처리할 수 없습니다. 현재 할인 금액 :" + paymentSource.getDiscountAmount();
//                log.error(msg);
//                throw new InvalidPaymentAmountException(msg);
//            }

            final String partnerCorpName = partnerCorporationService.getPartnerCorpName(paymentSource.getPartnerBusinessRegistrationNumber());

            return new Payment(
                null,
                    paymentSource.getFinalAmount(),
                    paymentSource.getPaymentDate(),
                    partnerCorpName,
                    "PAYMENT"
            );
        };
    }

//    private ItemWriter<Payment> itemWriter(){
//        return paymentRepository::saveAll;
//    }

//    @Bean
//    public JpaItemWriter<Payment> paymentJpaItemWriter(){
//        JpaItemWriter<Payment> writer = new JpaItemWriter<>();
//        writer.setEntityManagerFactory(entityManagerFactory);
//        return writer;
//    }

    @Bean
    public ItemWriter<Payment> paymentReportWriter(){
        return chunk -> {
            for(Payment payment:chunk){
                log.info("Writer payment: {}",payment);
            }
        };
    }


}
