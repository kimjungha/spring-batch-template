package org.example.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchstudy.utils.TimeUtils;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChunkDurationTrackerListener implements ChunkListener {

    private final TimeUtils timeUtils;

    @Override
    public void beforeChunk(ChunkContext context){
        context.setAttribute("startTime", System.currentTimeMillis());
    }

    @Override
    public void afterChunk(ChunkContext context){
        Object startTimeAttr = context.getAttribute("startTime");
        if (startTimeAttr == null) return;

        final long startTime = (long) startTimeAttr;
        final long endTime = System.currentTimeMillis();
        final long durationInMillis = endTime - startTime;

        // 처리 완료된 청크 번호를 가져옵니다.
        final long commitCount = context.getStepContext().getStepExecution().getCommitCount();
        String durationStr = timeUtils.convertDuration(durationInMillis);
        log.info("Chunk {} 처리 완료. 소요 시간: {}", commitCount, durationStr);
    }
}
