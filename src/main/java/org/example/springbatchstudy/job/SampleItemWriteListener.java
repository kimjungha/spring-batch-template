package org.example.springbatchstudy.job;

import lombok.extern.slf4j.Slf4j;
import org.example.springbatchstudy.entity.Payment;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@Slf4j
public class SampleItemWriteListener implements ItemWriteListener<Payment> {

    @Override
    public void beforeWrite(Chunk<? extends Payment> items) {
         log.info("sample -4 SampleItemWriteListener#beforeWrite");
    }

    @Override
    public void afterWrite(Chunk<? extends Payment> items) {
         log.info("sample -4 SampleItemWriteListener#afterWrite");
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends Payment> items) {
         log.info("sample -4 SampleItemWriteListener#onWriteError");
    }
}
