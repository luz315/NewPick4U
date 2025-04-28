package com.newpick4u.news.news.application.scheduler;

import com.newpick4u.news.news.application.usecase.NewsVectorQueueOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsVectorScheduler {

    private final NewsVectorQueueOperator newsVectorQueueOperator;

    @Scheduled(fixedDelay = 3600000) // 1시간마다 실행
    public void processPendingNewsVectors() {
        try {
            newsVectorQueueOperator.flushAndGeneratePendingVectors();
            log.info("[뉴스 벡터 생성 스케줄러] 처리 완료");
        } catch (Exception e) {
            log.error("[뉴스 벡터 생성 실패]", e);
        }
    }
}
