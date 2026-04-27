package org.example.springbatchstudy.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class SampleChuckListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("sample - 1 SampleChuckListener#beforeChunk ");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("sample - 1 SampleChuckListener#afterChunk ");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.info("sample - 1 SampleChuckListener#afterChunkError ");
    }
}
