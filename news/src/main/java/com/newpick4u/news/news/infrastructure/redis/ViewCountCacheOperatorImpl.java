package com.newpick4u.news.news.infrastructure.redis;

import com.newpick4u.news.news.application.usecase.ViewCountCacheOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ViewCountCacheOperatorImpl implements ViewCountCacheOperator {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String DAILY_VIEW_KEY = "view:user:%s:news:%s";
    private static final String NEWS_VIEW_COUNT_KEY = "news:view:%s";

    @Override
    public boolean canIncreaseView(UUID newsId, Long userId) {
        String key = String.format(DAILY_VIEW_KEY, userId, newsId);
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            return false;
        }
        redisTemplate.opsForValue().set(key, "1", Duration.ofDays(1));
        return true;
    }

    @Override
    public void incrementViewCount(UUID newsId) {
        String key = String.format(NEWS_VIEW_COUNT_KEY, newsId);
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public long getViewCount(UUID newsId) {
        String key = String.format(NEWS_VIEW_COUNT_KEY, newsId);
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Long.parseLong(value);
    }

    @Override
    public void clearDailyViewKey(UUID newsId, Long userId) {
        String key = String.format(DAILY_VIEW_KEY, userId, newsId);
        redisTemplate.delete(key);
    }
}