package com.newpick4u.news.news.application.usecase;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ViewCountCacheOperator {
    boolean isViewToday(UUID newsId, Long userId);
    void incrementViewCount(UUID newsId);
    long getViewCount(UUID newsId);
    void clearDailyViewKey(UUID newsId, Long userId);
    void updatePopularityScore(UUID newsId, long viewCount, LocalDateTime createdAt);
    Set<String> getTopPopularNewsIds(int limit);
}