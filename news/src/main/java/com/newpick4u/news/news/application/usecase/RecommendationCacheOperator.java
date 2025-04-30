package com.newpick4u.news.news.application.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface RecommendationCacheOperator {
    void incrementUserTagScore(Long userId, List<String> tags);
    Map<String, Double> getUserTagScore(Long userId);
    List<String> getRecommendedNews(Long userId);
    void storeRecommendedNews(Long userId, List<String> newsIds);
    Set<Long> getCachedUserIds();
    List<String> getGlobalTagIndexList();
    void cacheFallbackLatestNews(List<UUID> newsIds);
    List<UUID> getFallbackLatestNews();
}