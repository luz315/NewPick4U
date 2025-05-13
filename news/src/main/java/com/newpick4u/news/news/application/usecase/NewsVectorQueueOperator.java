package com.newpick4u.news.news.application.usecase;

import java.util.UUID;

public interface NewsVectorQueueOperator {
    void enqueueNewsVector(UUID newsId);     // 대기열에 등록
    void removeFromQueue(UUID newsId);       // 처리 후 제거
    void flushAndGeneratePendingVectors();   // 스케줄러에서 호출
}
