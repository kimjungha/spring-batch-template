package org.example.springbatchstudy.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class JobDurationTrackerListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {

        log.info(" >>> JOb Start : {}, 시작 시간 :{}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStartTime()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        final LocalDateTime startTime = jobExecution.getStartTime();
        log.info("Job Start Time :{}", startTime);

        final LocalDateTime createdTime = jobExecution.getCreateTime();
        log.info("Job Create Time :{}", createdTime);

        final LocalDateTime endTime = jobExecution.getEndTime();
        final long durationMillis = Duration.between(startTime,endTime).toMillis();
        final long seconds = (durationMillis % (1_000 * 60))  / 1_000;

        log.info(" >>> Job 종료 : 상태 ={}, 총 소요시각 = {}, 종료시각 = {}",
                jobExecution.getStatus(),
                seconds,
                endTime
                );

        if(jobExecution.getStatus().isUnsuccessful()){
            log.error(" >>> Job 실패 원인 : {}", jobExecution.getJobInstance().getJobName());
        }
    }
}
