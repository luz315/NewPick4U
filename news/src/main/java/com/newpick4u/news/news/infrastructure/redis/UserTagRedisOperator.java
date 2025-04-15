package com.newpick4u.news.news.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserTagRedisOperator {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_TAGS = 50;
    private static final Duration TAG_TTL = Duration.ofDays(30); // 30일간 미접속 시 태그 만료
    private static final Duration RECOMMEND_CACHE_TTL = Duration.ofDays(1);
    private static final String TAG_KEY_PATTERN = "user:*:tags";

    // 태그 카운트 증가 (조회 시 호출)
    public void incrementUserTags(Long userId, List<String> tagNames) {
        String key = buildKey(userId);
        for (String tag : tagNames) {
            redisTemplate.opsForZSet().incrementScore(key, tag, 1);
        }
        trimTagLimit(key);
        redisTemplate.expire(key, TAG_TTL);
    }

    // 유저 태그 점수 맵 가져오기
    public Map<String, Double> getUserTagScoreMap(Long userId) {
        String key = buildKey(userId);
        Set<ZSetOperations.TypedTuple<String>> rawTags =
                redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);

        if (rawTags == null) return Collections.emptyMap();

        Map<String, Double> tagScoreMap = new HashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple : rawTags) {
            if (tuple.getValue() != null && tuple.getScore() != null) {
                tagScoreMap.put(tuple.getValue(), tuple.getScore());
            }
        }
        return tagScoreMap;
    }

    // 태그 개수 제한 초과 시 오래된 태그 제거
    private void trimTagLimit(String key) {
        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size != null && size > MAX_TAGS) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - MAX_TAGS - 1);
        }
    }

    private String buildKey(Long userId) {
        return "user:" + userId + ":tags";
    }

    // 추천 캐시 키
    private String recommendKey(Long userId) {
        return "user:" + userId + ":recommend";
    }

    // 추천 뉴스 캐시 저장
    public void cacheRecommendedNews(Long userId, List<String> newsIds) {
        String key = recommendKey(userId);
        redisTemplate.delete(key); // 덮어쓰기
        redisTemplate.opsForList().rightPushAll(key, newsIds);
        redisTemplate.expire(key, RECOMMEND_CACHE_TTL);
    }

    // 추천 뉴스 캐시 조회
    public List<String> getCachedRecommendedNews(Long userId) {
        String key = recommendKey(userId);
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    // 추천 캐시용 사용자 ID 목록 조회 (SCAN 기반)
    public Set<Long> getAllUserIds() {
        Set<String> keys = redisTemplate.keys(TAG_KEY_PATTERN);
        if (keys == null || keys.isEmpty()) return Set.of();

        return keys.stream()
                .map(this::extractUserIdFromKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Long extractUserIdFromKey(String key) {
        try {
            String[] parts = key.split(":" );
            return Long.parseLong(parts[1]);
        } catch (Exception e) {
            return null;
        }
    }
}