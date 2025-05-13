package com.newpick4u.news.news.application.scheduler;

import com.newpick4u.common.exception.CustomException;
import com.newpick4u.news.global.exception.NewsErrorCode;
import com.newpick4u.news.news.application.usecase.TagIndexQueueOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagIndexSyncScheduler {

    private final TagIndexQueueOperator tagIndexQueueOperator;

    @Scheduled(fixedDelay = 3600000) // 1시간마다 실행
    public void syncPendingTagsToGlobalIndex() {
        try {
            tagIndexQueueOperator.flushPendingTagsToGlobalIndex();
            log.info("[태그 인덱스 갱신] 완료");
        } catch (Exception e) {
            log.error("[태그 인덱스 갱신 실패]", e);
            throw CustomException.from(NewsErrorCode.REDIS_SCAN_FAIL);
        }
    }
}
