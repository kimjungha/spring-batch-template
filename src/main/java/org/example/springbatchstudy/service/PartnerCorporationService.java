package org.example.springbatchstudy.service;

/*
* 실제 http 통신은 아니며, delay 를 주어 http 통신하는것처럼 동작
* 랜덤으로 예외가 발생
*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PartnerCorporationService {

    private int failureCount = 0;
    private static final String TIMEOUT_ERROR_MESSAGE = "파트너 API 연결 실패 : 타임아웃 발생";
    private static final int HTTP_REQUEST_DELAY_MS = 200;

    public static final Map<String, String> PARTNER_CORP = Map.ofEntries(
            Map.entry("000-01-00002", "LG전자"),
            Map.entry("000-01-00003", "현대자동차"),
            Map.entry("000-01-00001", "삼성전자"),
            Map.entry("000-01-00004", "SK텔레콤"),
            Map.entry("000-01-00005", "네이버"),
            Map.entry("000-01-00006", "카카오"),
            Map.entry("000-01-00007", "쿠팡"),
            Map.entry("000-01-00008", "배달의민족"),
            Map.entry("000-01-00009", "토스"),
            Map.entry("000-01-00010", "당근마켓"),
            Map.entry("000-01-00011", "KT"),
            Map.entry("000-01-00012", "롯데그룹"),
            Map.entry("000-01-00013", "포스코"),
            Map.entry("000-01-00014", "신한금융그룹"),
            Map.entry("000-01-00015", "KB금융그룹"),
            Map.entry("000-01-00016", "농협"),
            Map.entry("000-01-00017", "하나금융그룹"),
            Map.entry("000-01-00018", "대한항공"),
            Map.entry("000-01-00019", "아시아나항공"),
            Map.entry("000-01-00020", "CJ그룹")
    );// 실제 http x , mock 객체처럼 사업자 번호를 상호명으로 변경


    public String getPartnerCorpName(String businessNumber) throws InterruptedException {
        final String partnerCorpName = PARTNER_CORP.getOrDefault(businessNumber,"NONE");
//        checkFailureByCallCount();
        log.info("파트너  사업자번호{}의 회사명 조회 성공:{}",businessNumber,partnerCorpName);
        return partnerCorpName;
    }

    /*
    * 호출 횟수에 따라 예외 발생 여부를 결정하는 메서드
    * failureCount 횟수마다 한번씩 예외를 발생시킨다.
    */
    private void checkFailureByCallCount() throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(HTTP_REQUEST_DELAY_MS);

        if(Math.random()< 0.1){
            failureCount++;
            log.warn("{} 번째 호출에서 랜덤하게 예외 발생",failureCount);
            throw new PartnerHttpException(TIMEOUT_ERROR_MESSAGE);
        }
    }

}
