package com.newpick4u.news.news.application.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TagLogCacheOperator {
    void incrementUserTags(Long userId, List<String> tags);
    Map<String, Double> getUserTagScoreMap(Long userId);
    List<String> getCachedRecommendedNews(Long userId);
    void cacheRecommendedNews(Long userId, List<String> newsIds);
    Set<Long> getAllUserIds();
}