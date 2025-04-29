package com.newpick4u.news.news.application.usecase;

import java.util.UUID;

public interface ViewCountCacheOperator {
    boolean canIncreaseView(UUID newsId, Long userId);
    void incrementViewCount(UUID newsId);
    long getViewCount(UUID newsId);
    void clearDailyViewKey(UUID newsId, Long userId);
}