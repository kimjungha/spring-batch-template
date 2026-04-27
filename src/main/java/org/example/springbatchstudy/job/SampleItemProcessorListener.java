package org.example.springbatchstudy.job;

import lombok.extern.slf4j.Slf4j;
import org.example.springbatchstudy.entity.Payment;
import org.example.springbatchstudy.entity.PaymentSource;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class SampleItemProcessorListener implements ItemProcessListener<PaymentSource, Payment> {

    @Override
    public void beforeProcess(PaymentSource item) {
          log.info("sample -3 SampleItemProcessorListener#beforeProcess");
    }

    @Override
    public void afterProcess(PaymentSource item, Payment result) {
         log.info("sample - 3 SampleItemProcessorListener#afterProcess");
    }

    @Override
    public void onProcessError(PaymentSource item, Exception e) {
           log.info("sample 3 SampleItemProcessorListener#onProcessError");
    }
}
