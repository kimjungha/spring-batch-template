package org.example.springbatchstudy.job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchstudy.utils.TimeUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class StepDurationTrackerListener implements StepExecutionListener {

    private final TimeUtils timeUtils;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info(">>> Step 시작: {} (Job={}, 시작 시각: {})",
                stepExecution.getStepName(),
                stepExecution.getJobExecution().getJobInstance().getJobName(),
                stepExecution.getStartTime());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(stepExecution.getStartTime() == null || stepExecution.getEndTime() == null) {
            log.warn(">>> Step의 시작 시각 또는 종료 시각이 null입니다. 소요 시간 계산이 불가능합니다.");
            return stepExecution.getExitStatus();
        }
        final LocalDateTime startTime = stepExecution.getStartTime();
        final LocalDateTime endTime = stepExecution.getEndTime();
        long durationMillis = ChronoUnit.MILLIS.between(startTime, endTime);

        String durationStr = timeUtils.convertDuration(durationMillis);
        log.info(
                ">>> Step 종료: {}, 상태={}, 읽음={}건, 처리={}건, 기록={}건, 스킵={}건, 소요시간={}",
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                stepExecution.getReadCount(),
                stepExecution.getProcessSkipCount() + stepExecution.getWriteCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                durationStr
        );

        // 스킵 발생 시 커스텀 ExitStatus 설정
        if (stepExecution.getSkipCount() > 0) {
            log.warn(">>> Step 내 일부 아이템 처리 누락(스킵) 발생");
            return new ExitStatus("COMPLETED WITH SKIPS");
        }
        return stepExecution.getExitStatus();
    }
}